package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.API.ApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserExploreFragment extends Fragment {

    private static final String TAG = "UserExploreFragment";

    private RecyclerView rvActivities, rvFilters;
    private EditText etSearch;
    private ActividadesApiAdapter adapter;

    // Datos
    private List<ActividadResponse> masterList = new ArrayList<>();
    private String currentSearchText = "";
    private String currentCategory = "Todos";

    // Filtros
    private FilterAdapter filterAdapter;
    private List<String> filterCategories = new ArrayList<>();

    public UserExploreFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflar el layout correspondiente al fragmento
        return inflater.inflate(R.layout.fragment_user_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas y configuraciones
        initViews(view);
        setupFilters();
        setupSearch();
        loadActividades();
    }

    private void initViews(View view) {
        rvActivities = view.findViewById(R.id.rvActivities);
        rvFilters = view.findViewById(R.id.rvFilters);
        etSearch = view.findViewById(R.id.etSearch);

        // Configuración de RecyclerView de Actividades
        rvActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActividadesApiAdapter(new ArrayList<>(), this::onActividadClick);
        rvActivities.setAdapter(adapter);

        // Configuración inicial de Filtros (para evitar 'No adapter attached')
        rvFilters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        filterCategories.add("Todos");
        filterAdapter = new FilterAdapter(filterCategories, category -> {
            currentCategory = category;
            applyFilters();
        });
        rvFilters.setAdapter(filterAdapter);
    }

    private void loadActividades() {
        ApiClient.getService().getActividades().enqueue(new Callback<List<ActividadResponse>>() {
            @Override
            public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                if (!isAdded())
                    return;

                if (response.isSuccessful() && response.body() != null) {
                    masterList = response.body();
                    applyFilters(); // Esto actualizará la lista en pantalla
                    Log.d(TAG, "Cargadas " + masterList.size() + " actividades");
                } else {
                    Log.e(TAG, "Error loading activities: " + response.code());
                    Toast.makeText(getContext(), "Error al cargar actividades", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ActividadResponse>> call, Throwable t) {
                if (!isAdded())
                    return;
                Log.e(TAG, "Error de conexión/Actividades", t);
                // No mostrar Toast de error aquí si ya tenemos datos cacheados o para no
                // molestar,
                // pero si la lista está vacía sí.
                if (masterList.isEmpty()) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadTiposVoluntariado() {
        ApiClient.getService().getTiposVoluntariado()
                .enqueue(new Callback<List<org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse>>() {
                    @Override
                    public void onResponse(
                            Call<List<org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse>> call,
                            Response<List<org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse>> response) {
                        if (!isAdded())
                            return;

                        List<String> newCategories = new ArrayList<>();
                        newCategories.add("Todos");

                        if (response.isSuccessful() && response.body() != null) {
                            for (org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse tipo : response
                                    .body()) {
                                String nombre = tipo.getNombre();
                                Log.d(TAG, "Tipo recibido API: " + nombre);
                                if (nombre != null && !nombre.trim().isEmpty()) {
                                    newCategories.add(nombre);
                                }
                            }
                        }

                        // Si la API no devolvió nada útil o falló parcialmente, asegurar filtros
                        // mínimos
                        if (newCategories.size() <= 1) {
                            Log.w(TAG, "API devolvió lista vacía de tipos. Usando fallback.");
                            newCategories.addAll(
                                    Arrays.asList("Social", "Medioambiente", "Educación", "Deporte", "Cultural"));
                        }

                        updateFilters(newCategories);
                    }

                    @Override
                    public void onFailure(
                            Call<List<org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse>> call,
                            Throwable t) {
                        if (!isAdded())
                            return;
                        List<String> fallback = new ArrayList<>();
                        fallback.add("Todos");
                        fallback.addAll(Arrays.asList("Social", "Medioambiente", "Educación", "Deporte", "Cultural"));
                        updateFilters(fallback);
                    }
                });
    }

    private void updateFilters(List<String> newCategories) {
        // Asumiendo que FilterAdapter tiene un método para actualizar datos,
        // si no, creamos uno nuevo o modificamos la lista si es observable.
        // Como FilterAdapter suele ser simple, lo recreamos o notificamos.
        // Lo más limpio es recrearlo o tener un método updateData en FilterAdapter.
        // Dado que no tengo el código de FilterAdapter a mano, recreo el adapter o uso
        // la lista local.

        filterCategories.clear();
        filterCategories.addAll(newCategories);
        filterAdapter.notifyDataSetChanged();
    }

    private void setupFilters() {
        loadTiposVoluntariado();
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().toLowerCase();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void applyFilters() {
        List<ActividadResponse> filtered = new ArrayList<>();
        for (ActividadResponse item : masterList) {
            // Filtrado por texto de búsqueda (Título)
            boolean matchesSearch = item.getTitulo().toLowerCase().contains(currentSearchText);

            // Filtrado por categoría (Tipo)
            // Usamos contains para coincidir con tipos parciales (ej: "Medioambiente"
            // coincide con "Medioambiente tecnologico")
            boolean matchesCategory = currentCategory.equals("Todos") ||
                    (item.getTipo() != null && item.getTipo().toLowerCase().contains(currentCategory.toLowerCase()));

            if (matchesSearch && matchesCategory) {
                filtered.add(item);
            }
        }

        // Actualizar el adaptador con la lista filtrada
        adapter.updateData(filtered);
        Log.d(TAG, "Filtrados: " + filtered.size() + " de " + masterList.size() + " (cat=" + currentCategory + ")");
    }

    private void onActividadClick(ActividadResponse actividad, int position) {
        // Abrir DetailActivity pasando el objeto actividad
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("actividad", actividad);
        startActivity(intent);
    }
}