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

        // Usamos getContext() en lugar de 'this'
        rvActivities.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializamos el adapter con una lista vacía y el listener de clic
        adapter = new ActividadesApiAdapter(new ArrayList<>(), this::onActividadClick);
        rvActivities.setAdapter(adapter);
    }

    private void loadActividades() {
        ApiClient.getService().getActividades().enqueue(new Callback<List<ActividadResponse>>() {
            @Override
            public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                if (!isAdded()) return; // Evitar crash si el fragmento ya no está activo

                if (response.isSuccessful() && response.body() != null) {
                    masterList = response.body();
                    applyFilters();
                    Log.d(TAG, "Cargadas " + masterList.size() + " actividades");
                } else {
                    Log.e(TAG, "Error: " + response.code());
                    Toast.makeText(getContext(), "Error al cargar actividades", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ActividadResponse>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e(TAG, "Error de conexión", t);
                Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilters() {
        List<String> categories = Arrays.asList("Todos", "Social", "Medioambiente", "Educación", "Deporte", "General");

        // Configuración horizontal para los filtros
        rvFilters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        FilterAdapter filterAdapter = new FilterAdapter(categories, category -> {
            currentCategory = category;
            applyFilters();
        });
        rvFilters.setAdapter(filterAdapter);
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
            // Usamos contains para coincidir con tipos parciales (ej: "Medioambiente" coincide con "Medioambiente tecnologico")
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