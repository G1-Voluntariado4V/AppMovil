package org.cuatrovientos.voluntariado4v.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.voluntariado4v.Adapters.ActivitiesAdapter;
import org.cuatrovientos.voluntariado4v.Adapters.FilterAdapter;
import org.cuatrovientos.voluntariado4v.App.MockDataProvider;
import org.cuatrovientos.voluntariado4v.Models.ActivityModel;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserActivitiesFragment extends Fragment {

    // Vistas
    private RecyclerView rvActivas, rvHistorial, rvFilters;
    private TextView tabActivas, tabHistorial, tvEmptyState;
    private EditText etSearch;

    // Datos
    private ArrayList<ActivityModel> masterListActivas;
    private ArrayList<ActivityModel> masterListHistorial;

    // Estado de filtros
    private String currentSearchText = "";
    private String currentCategory = "Todos";

    public UserActivitiesFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflamos el layout del fragmento (asegúrate de que el XML se llame fragment_user_activities)
        return inflater.inflate(R.layout.fragment_user_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadData();

        // Configuración de componentes
        setupFilters(); // Configura la lista horizontal de filtros
        setupSearch();  // Configura el buscador
        setupTabs();    // Configura los clics en los tabs (Activas/Historial)

        updateLists();  // Primera carga de datos visual
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
        // Cargamos los datos desde el MockProvider
        masterListActivas = MockDataProvider.getMyActivities();
        masterListHistorial = MockDataProvider.getHistoryActivities();
    }

    private void setupFilters() {
        List<String> categories = Arrays.asList("Todos", "Social", "Medioambiente", "Educación", "Deporte");

        // Usamos getContext() para el LayoutManager
        rvFilters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        FilterAdapter filterAdapter = new FilterAdapter(categories, category -> {
            currentCategory = category;
            updateLists();
        });
        rvFilters.setAdapter(filterAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().toLowerCase();
                updateLists();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateLists() {
        // Filtramos las listas basándonos en la búsqueda y categoría actual
        ArrayList<ActivityModel> filteredActivas = filterList(masterListActivas);
        ArrayList<ActivityModel> filteredHistorial = filterList(masterListHistorial);

        // Actualizar RecyclerView de Activas
        rvActivas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActivas.setAdapter(new ActivitiesAdapter(filteredActivas, ActivitiesAdapter.TYPE_BIG_CARD, null));

        // Actualizar RecyclerView de Historial
        rvHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistorial.setAdapter(new ActivitiesAdapter(filteredHistorial, ActivitiesAdapter.TYPE_BIG_CARD, null));

        // Comprobar si hay que mostrar el estado vacío (dependiendo del tab seleccionado)
        if (tabActivas.getCurrentTextColor() == Color.parseColor("#3D5AFE")) {
            checkEmptyState(filteredActivas);
        } else {
            checkEmptyState(filteredHistorial);
        }
    }

    private ArrayList<ActivityModel> filterList(ArrayList<ActivityModel> source) {
        ArrayList<ActivityModel> result = new ArrayList<>();
        if (source == null) return result;

        for (ActivityModel item : source) {
            boolean matchesSearch = item.getTitle().toLowerCase().contains(currentSearchText);
            boolean matchesCategory = currentCategory.equals("Todos") || item.getCategory().equalsIgnoreCase(currentCategory);

            if (matchesSearch && matchesCategory) {
                result.add(item);
            }
        }
        return result;
    }

    private void setupTabs() {
        tabActivas.setOnClickListener(v -> {
            updateTabStyles(true);
            rvActivas.setVisibility(View.VISIBLE);
            rvHistorial.setVisibility(View.GONE);

            // Verificamos si la lista actual tiene datos para mostrar u ocultar el mensaje de vacío
            ActivitiesAdapter adapter = (ActivitiesAdapter) rvActivas.getAdapter();
            if (adapter != null) {
                checkEmptyState(adapter.getDataList());
            }
        });

        tabHistorial.setOnClickListener(v -> {
            updateTabStyles(false);
            rvHistorial.setVisibility(View.VISIBLE);
            rvActivas.setVisibility(View.GONE);

            ActivitiesAdapter adapter = (ActivitiesAdapter) rvHistorial.getAdapter();
            if (adapter != null) {
                checkEmptyState(adapter.getDataList());
            }
        });
    }

    private void updateTabStyles(boolean isActivas) {
        if (isActivas) {
            // Estilo Tab Activas Seleccionado
            tabActivas.setBackgroundResource(R.drawable.bg_tab_selected);
            tabActivas.setTextColor(Color.parseColor("#3D5AFE"));

            // Estilo Tab Historial Deseleccionado
            tabHistorial.setBackground(null);
            tabHistorial.setTextColor(Color.parseColor("#667085"));
        } else {
            // Estilo Tab Historial Seleccionado
            tabHistorial.setBackgroundResource(R.drawable.bg_tab_selected);
            tabHistorial.setTextColor(Color.parseColor("#3D5AFE"));

            // Estilo Tab Activas Deseleccionado
            tabActivas.setBackground(null);
            tabActivas.setTextColor(Color.parseColor("#667085"));
        }
    }

    private void checkEmptyState(ArrayList<ActivityModel> list) {
        if (list == null || list.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvActivas.setVisibility(View.GONE);
            rvHistorial.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            // Volvemos a mostrar el RecyclerView correspondiente al tab seleccionado
            if (tabActivas.getCurrentTextColor() == Color.parseColor("#3D5AFE")) {
                rvActivas.setVisibility(View.VISIBLE);
            } else {
                rvHistorial.setVisibility(View.VISIBLE);
            }
        }
    }
}