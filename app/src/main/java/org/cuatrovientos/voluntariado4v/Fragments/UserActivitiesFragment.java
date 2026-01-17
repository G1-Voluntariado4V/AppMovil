package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.voluntariado4v.Activities.DetailActivity;
import org.cuatrovientos.voluntariado4v.Adapters.ActividadesApiAdapter;
import org.cuatrovientos.voluntariado4v.Adapters.FilterAdapter;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.HistorialApiResponse;
import org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse;
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.API.VoluntariadoApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivitiesFragment extends Fragment {

    private static final String TAG = "UserActivitiesFragment";

    // Vistas
    private RecyclerView rvActivas, rvHistorial, rvFilters;
    private TextView tabActivas, tabHistorial, tvEmptyState;
    private EditText etSearch;

    // Datos
    private ArrayList<ActividadResponse> masterListActivas = new ArrayList<>();
    private ArrayList<ActividadResponse> masterListHistorial = new ArrayList<>();

    // Estado de filtros
    private String currentSearchText = "";
    private String currentCategory = "Todos";

    // Filtros dinámicos
    private FilterAdapter filterAdapter;
    private List<String> filterCategories = new ArrayList<>();

    public UserActivitiesFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        setupFilters();
        setupSearch();
        setupTabs();

        loadData();
    }

    private void initViews(View view) {
        rvActivas = view.findViewById(R.id.rvMyActivities);
        rvHistorial = view.findViewById(R.id.rvHistorial);
        rvFilters = view.findViewById(R.id.rvFilters);
        tabActivas = view.findViewById(R.id.tabActivas);
        tabHistorial = view.findViewById(R.id.tabHistorial);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        etSearch = view.findViewById(R.id.etSearch);
    }

    private void loadData() {
        if (getContext() == null)
            return;
        SharedPreferences prefs = getContext().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1)
            return;

        VoluntariadoApiService apiService = ApiClient.getService();
        Call<HistorialApiResponse> call = apiService.getHistorial(userId, userId);

        call.enqueue(new Callback<HistorialApiResponse>() {
            @Override
            public void onResponse(Call<HistorialApiResponse> call, Response<HistorialApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterListActivas.clear();
                    masterListHistorial.clear();

                    List<HistorialApiResponse.InscripcionItem> items = response.body().getActividades();
                    if (items != null) {
                        for (HistorialApiResponse.InscripcionItem item : items) {
                            ActividadResponse act = item.toActividadResponse();
                            String estado = item.getEstadoInscripcion();

                            if ("Pendiente".equalsIgnoreCase(estado) || "Aceptada".equalsIgnoreCase(estado)) {
                                masterListActivas.add(act);
                            } else {
                                masterListHistorial.add(act);
                            }
                        }
                    }
                    updateLists();
                }
            }

            @Override
            public void onFailure(Call<HistorialApiResponse> call, Throwable t) {
                // Manejar error (Toast o log)
                Log.e(TAG, "Error cargando historial", t);
            }
        });
    }

    private void setupFilters() {
        // Inicializar con "Todos" y cargar de API
        filterCategories.clear();
        filterCategories.add("Todos");

        rvFilters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        filterAdapter = new FilterAdapter(filterCategories, category -> {
            currentCategory = category;
            updateLists();
        });
        rvFilters.setAdapter(filterAdapter);

        loadTiposVoluntariado();
    }

    private void loadTiposVoluntariado() {
        ApiClient.getService().getTiposVoluntariado()
                .enqueue(new Callback<List<TipoVoluntariadoResponse>>() {
                    @Override
                    public void onResponse(Call<List<TipoVoluntariadoResponse>> call,
                            Response<List<TipoVoluntariadoResponse>> response) {
                        if (!isAdded())
                            return;

                        List<String> newCategories = new ArrayList<>();
                        newCategories.add("Todos");

                        if (response.isSuccessful() && response.body() != null) {
                            for (TipoVoluntariadoResponse tipo : response.body()) {
                                String nombre = tipo.getNombre();
                                if (nombre != null && !nombre.trim().isEmpty()) {
                                    newCategories.add(nombre);
                                }
                            }
                        } else {
                            // Fallback si falla API
                            newCategories.addAll(
                                    Arrays.asList("Social", "Medioambiente", "Educación", "Deporte", "Cultural"));
                        }

                        // Actualizar UI
                        filterCategories.clear();
                        filterCategories.addAll(newCategories);
                        filterAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<TipoVoluntariadoResponse>> call, Throwable t) {
                        // Fallback silencioso
                        if (isAdded() && filterCategories.size() <= 1) {
                            filterCategories.addAll(
                                    Arrays.asList("Social", "Medioambiente", "Educación", "Deporte", "Cultural"));
                            filterAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().toLowerCase();
                updateLists();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateLists() {
        ArrayList<ActividadResponse> filteredActivas = filterList(masterListActivas);
        ArrayList<ActividadResponse> filteredHistorial = filterList(masterListHistorial);

        ActividadesApiAdapter.OnItemClickListener listener = (item, position) -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("actividad", item);
            intent.putExtra("INSCRIPCION_STATUS", item.getEstadoInscripcionUsuario());
            startActivity(intent);
        };

        // Pasar true si estamos en pestaña Activas (azul)
        boolean isActivasTab = tabActivas.getCurrentTextColor() == Color.parseColor("#3D5AFE");

        if (isActivasTab) {
            rvActivas.setLayoutManager(new LinearLayoutManager(getContext()));
            rvActivas.setAdapter(new ActividadesApiAdapter(filteredActivas, listener));
            checkEmptyState(filteredActivas);

            rvActivas.setVisibility(filteredActivas.isEmpty() ? View.GONE : View.VISIBLE);
            rvHistorial.setVisibility(View.GONE);
        } else {
            rvHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
            rvHistorial.setAdapter(new ActividadesApiAdapter(filteredHistorial, listener));
            checkEmptyState(filteredHistorial);

            rvHistorial.setVisibility(filteredHistorial.isEmpty() ? View.GONE : View.VISIBLE);
            rvActivas.setVisibility(View.GONE);
        }
    }

    private ArrayList<ActividadResponse> filterList(ArrayList<ActividadResponse> source) {
        ArrayList<ActividadResponse> result = new ArrayList<>();
        if (source == null)
            return result;

        for (ActividadResponse item : source) {
            boolean matchesSearch = item.getTitulo().toLowerCase().contains(currentSearchText);

            // Lógica de filtrado mejorada (igual que Explorar)
            boolean matchesCategory = currentCategory.equals("Todos");
            if (!matchesCategory) {
                // Comprobar lista de tipos
                if (item.getTipos() != null) {
                    for (String tipo : item.getTipos()) {
                        if (tipo.toLowerCase().contains(currentCategory.toLowerCase()) ||
                                currentCategory.toLowerCase().contains(tipo.toLowerCase())) {
                            matchesCategory = true;
                            break;
                        }
                    }
                }
                // Comprobar también campo 'tipo' singular por compatibilidad
                if (!matchesCategory && item.getTipo() != null) {
                    if (item.getTipo().toLowerCase().contains(currentCategory.toLowerCase())) {
                        matchesCategory = true;
                    }
                }
            }

            if (matchesSearch && matchesCategory) {
                result.add(item);
            }
        }
        return result;
    }

    private void setupTabs() {
        tabActivas.setOnClickListener(v -> {
            updateTabStyles(true);
            updateLists();
        });

        tabHistorial.setOnClickListener(v -> {
            updateTabStyles(false);
            updateLists();
        });
    }

    private void updateTabStyles(boolean isActivas) {
        if (isActivas) {
            tabActivas.setBackgroundResource(R.drawable.bg_tab_selected);
            tabActivas.setTextColor(Color.parseColor("#3D5AFE"));
            tabHistorial.setBackground(null);
            tabHistorial.setTextColor(Color.parseColor("#667085"));
        } else {
            tabHistorial.setBackgroundResource(R.drawable.bg_tab_selected);
            tabHistorial.setTextColor(Color.parseColor("#3D5AFE"));
            tabActivas.setBackground(null);
            tabActivas.setTextColor(Color.parseColor("#667085"));
        }
    }

    private void checkEmptyState(List<ActividadResponse> list) {
        if (list == null || list.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }
}