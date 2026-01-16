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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.voluntariado4v.Activities.DetailActivity;
import org.cuatrovientos.voluntariado4v.Adapters.ActivitiesAdapter;
import org.cuatrovientos.voluntariado4v.Adapters.FilterAdapter;
import org.cuatrovientos.voluntariado4v.App.MockDataProvider;
import org.cuatrovientos.voluntariado4v.Models.ActivityModel;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrganizationActivitiesFragment extends Fragment {

    private static final String TAG = "OrgActivitiesFrag";

    private RecyclerView rvActivities, rvFilters;
    private EditText etSearch;
    private LinearLayout layoutEmptyState;
    private ActivitiesAdapter adapter;

    // Datos
    private ArrayList<ActivityModel> masterList = new ArrayList<>();
    private String currentSearchText = "";
    private String currentStatusFilter = "Todas"; // Filtro inicial por defecto

    public OrganizationActivitiesFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organization_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadInitialData();
        setupFilters();
        setupSearch();
    }

    private void initViews(View view) {
        rvActivities = view.findViewById(R.id.rvAllActivities);
        rvFilters = view.findViewById(R.id.rvFilters);
        etSearch = view.findViewById(R.id.etSearch);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        rvActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFilters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void loadInitialData() {
        // Cargar TODAS las actividades inicialmente
        masterList = MockDataProvider.getOrgActivitiesByStatus(null);

        adapter = new ActivitiesAdapter(new ArrayList<>(masterList), ActivitiesAdapter.TYPE_SMALL_CARD, (item, pos) -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("activity_data", item);
            startActivity(intent);
        });
        rvActivities.setAdapter(adapter);

        checkEmptyState(masterList.size());
    }

    private void setupFilters() {
        // Definimos la lista como pediste, manteniendo "Todos" para poder limpiar el filtro
        List<String> statusOptions = Arrays.asList("Todas", "Activas", "Pendientes", "Finalizadas", "Canceladas");

        FilterAdapter filterAdapter = new FilterAdapter(statusOptions, selectedStatus -> {
            currentStatusFilter = selectedStatus;
            applyFilters();
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
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilters() {
        ArrayList<ActivityModel> filtered = new ArrayList<>();

        for (ActivityModel item : masterList) {
            // 1. Filtrado por texto
            boolean matchesSearch = item.getTitle().toLowerCase().contains(currentSearchText);

            // 2. Filtrado por Estado
            boolean matchesStatus = false;

            if (currentStatusFilter.equals("Todos")) {
                matchesStatus = true;
            } else {
                // Obtenemos el estado del objeto y lo convertimos a mayúsculas para comparar
                String itemStatus = item.getStatus() != null ? item.getStatus().toUpperCase() : "";

                switch (currentStatusFilter) {
                    case "Activas":
                        matchesStatus = itemStatus.equals("ACTIVE");
                        break;
                    case "Pendientes":
                        matchesStatus = itemStatus.equals("PENDING"); // Asumimos que el backend usa PENDING
                        break;
                    case "Finalizadas":
                        matchesStatus = itemStatus.equals("FINISHED");
                        break;
                    case "Canceladas":
                        matchesStatus = itemStatus.equals("CANCELLED");
                        break;
                    default:
                        matchesStatus = true;
                }
            }

            if (matchesSearch && matchesStatus) {
                filtered.add(item);
            }
        }

        // Actualizar adaptador y verificar estado vacío
        adapter.updateData(filtered);
        checkEmptyState(filtered.size());

        Log.d(TAG, "Filtrados: " + filtered.size() + " (Estado=" + currentStatusFilter + ")");
    }

    private void checkEmptyState(int listSize) {
        if (listSize == 0) {
            rvActivities.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvActivities.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
}