package org.cuatrovientos.voluntariado4v.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.API.VoluntariadoApiService;
import org.cuatrovientos.voluntariado4v.Activities.DetailActivity;
import org.cuatrovientos.voluntariado4v.Activities.EditActividad;
import org.cuatrovientos.voluntariado4v.Adapters.CoordinatorActivitiesAdapter;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.EstadoRequest;
import org.cuatrovientos.voluntariado4v.Models.MensajeResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorActivitiesFragment extends Fragment implements CoordinatorActivitiesAdapter.OnActivityActionListener {

    // Vistas
    private TabLayout tabLayout;
    private RecyclerView rvPending, rvAllActivities;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private EditText etSearch;

    // Adaptadores
    private CoordinatorActivitiesAdapter pendingAdapter;
    private CoordinatorActivitiesAdapter allAdapter;

    // Datos
    private VoluntariadoApiService apiService;
    private List<ActividadResponse> masterList = new ArrayList<>();
    private int currentAdminId;

    // Estado
    private String currentSearchText = "";
    private int currentTabPosition = 0; // 0 = Catálogo, 1 = Solicitudes

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_activities, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        currentAdminId = prefs.getInt("user_id", -1);

        initViews(view);
        setupAdapters();
        setupSearch();
        setupTabs();

        try {
            apiService = ApiClient.getService();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (apiService != null) {
            loadData();
        }
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayoutActivities);
        rvPending = view.findViewById(R.id.rvPendingActivities);
        rvAllActivities = view.findViewById(R.id.rvAllActivities);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        etSearch = view.findViewById(R.id.etSearchActivity);
    }

    private void setupAdapters() {
        // Pestaña SOLICITUDES (Pendientes)
        pendingAdapter = new CoordinatorActivitiesAdapter(getContext(), CoordinatorActivitiesAdapter.TYPE_PENDING, this);
        rvPending.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPending.setAdapter(pendingAdapter);

        // Pestaña CATÁLOGO (Resto)
        allAdapter = new CoordinatorActivitiesAdapter(getContext(), CoordinatorActivitiesAdapter.TYPE_CATALOG, this);
        rvAllActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAllActivities.setAdapter(allAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().toLowerCase();
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupTabs() {
        // Configuramos las pestañas
        tabLayout.addTab(tabLayout.newTab().setText("Catálogo"));
        tabLayout.addTab(tabLayout.newTab().setText("Solicitudes"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                updateVisibility();
                // Importante: Volver a aplicar filtros al cambiar de pestaña para refrescar la vista
                applyFilters();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
        updateVisibility();
    }

    private void updateVisibility() {
        // Si estamos en Catálogo (Posición 0)
        if (currentTabPosition == 0) {
            rvPending.setVisibility(View.GONE);
            rvAllActivities.setVisibility(View.VISIBLE);
        } else {
            // Si estamos en Solicitudes (Posición 1)
            rvPending.setVisibility(View.VISIBLE);
            rvAllActivities.setVisibility(View.GONE);
        }
    }

    // ---------------- API Calls ----------------

    private void loadData() {
        showLoading(true);
        apiService.getAllActivitiesCoord(currentAdminId).enqueue(new Callback<List<ActividadResponse>>() {
            @Override
            public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    masterList = response.body();
                    applyFilters();
                } else {
                    toggleEmptyState(true);
                }
            }

            @Override
            public void onFailure(Call<List<ActividadResponse>> call, Throwable t) {
                showLoading(false);
                toggleEmptyState(true);
            }
        });
    }

    private void applyFilters() {
        List<ActividadResponse> pendingList = new ArrayList<>();
        List<ActividadResponse> catalogList = new ArrayList<>();

        for (ActividadResponse act : masterList) {
            // 1. Filtro de Texto (Buscador)
            String titulo = act.getTitulo() != null ? act.getTitulo().toLowerCase() : "";
            String org = act.getNombreOrganizacion() != null ? act.getNombreOrganizacion().toLowerCase() : "";

            if (!currentSearchText.isEmpty() && !titulo.contains(currentSearchText) && !org.contains(currentSearchText)) {
                continue;
            }

            // 2. Separación Lógica por Estado
            String estado = act.getEstadoPublicacion();

            // Verificamos si es null para evitar crash
            if (estado != null) {
                // Comprobamos variaciones de "En revisión" (con acento, sin acento, mayúsculas...)
                if (estado.equalsIgnoreCase("En revision") ||
                        estado.equalsIgnoreCase("En revisión") ||
                        estado.equalsIgnoreCase("Pendiente")) {

                    pendingList.add(act); // Va a la pestaña Solicitudes

                } else {
                    // "Publicada", "Rechazada", "Cancelada", "Finalizada" -> Van al Catálogo
                    catalogList.add(act);
                }
            } else {
                // Si el estado es null, por defecto al catálogo (o podrías decidir ocultarlo)
                catalogList.add(act);
            }
        }

        // 3. Asignar a los adaptadores según la pestaña actual
        if (currentTabPosition == 0) {
            // Pestaña Catálogo
            allAdapter.setActivitiesList(catalogList);
            toggleEmptyState(catalogList.isEmpty());
        } else {
            // Pestaña Solicitudes
            pendingAdapter.setActivitiesList(pendingList);
            toggleEmptyState(pendingList.isEmpty());
        }
    }

    // ---------------- Acciones del Adaptador ----------------

    @Override
    public void onPublish(int id) {
        changeStatus(id, "Publicada");
    }

    @Override
    public void onReject(int id) {
        changeStatus(id, "Rechazada");
    }

    @Override
    public void onDelete(int id) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Actividad")
                .setMessage("¿Estás seguro de eliminar esta actividad definitivamente?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteActivity(id))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onEdit(ActividadResponse actividad) {
        Intent intent = new Intent(getContext(), EditActividad.class);
        intent.putExtra("actividad", actividad);
        startActivity(intent);
    }

    @Override
    public void onClick(ActividadResponse actividad) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("actividad", actividad);
        intent.putExtra("IS_ORG_VIEW", true);
        intent.putExtra("IS_COORDINATOR", true);
        startActivity(intent);
    }

    private void changeStatus(int id, String nuevoEstado) {
        apiService.updateActivityStatus(currentAdminId, id, new EstadoRequest(nuevoEstado))
                .enqueue(new Callback<MensajeResponse>() {
                    @Override
                    public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Estado actualizado a " + nuevoEstado, Toast.LENGTH_SHORT).show();
                            loadData(); // Recargar listas
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<MensajeResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteActivity(int id) {
        apiService.deleteActivityCoord(currentAdminId, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Actividad eliminada", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading && layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
    }

    private void toggleEmptyState(boolean isEmpty) {
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}