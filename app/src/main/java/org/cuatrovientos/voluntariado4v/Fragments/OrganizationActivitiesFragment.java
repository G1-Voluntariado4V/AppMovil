package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import org.cuatrovientos.voluntariado4v.Activities.EditActividad;
import org.cuatrovientos.voluntariado4v.Adapters.ActividadesApiAdapter;
import org.cuatrovientos.voluntariado4v.Adapters.FilterAdapter;
import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizationActivitiesFragment extends Fragment {

    private static final String TAG = "OrgActivitiesFrag";

    private RecyclerView rvActivities, rvFilters;
    private EditText etSearch;
    private LinearLayout layoutEmptyState;
    private ActividadesApiAdapter adapter;

    private ArrayList<ActividadResponse> masterList = new ArrayList<>();
    private String currentSearchText = "";
    private String currentStatusFilter = "Todas";
    private int orgId;

    public OrganizationActivitiesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organization_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        orgId = prefs.getInt("user_id", -1);

        initViews(view);
        setupFilters();
        setupSearch();
        loadActividades();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActividades();
    }

    private void initViews(View view) {
        rvActivities = view.findViewById(R.id.rvAllActivities);
        rvFilters = view.findViewById(R.id.rvFilters);
        etSearch = view.findViewById(R.id.etSearch);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        rvActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFilters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void loadActividades() {
        if (orgId == -1)
            return;

        ApiClient.getService().getActividadesOrganizacion(orgId).enqueue(new Callback<List<ActividadResponse>>() {
            @Override
            public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                if (!isAdded())
                    return;

                if (response.isSuccessful() && response.body() != null) {
                    masterList.clear();
                    masterList.addAll(response.body());
                    applyFilters();
                } else {
                    Log.e(TAG, "Error cargando actividades: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ActividadResponse>> call, Throwable t) {
                Log.e(TAG, "Error de conexión", t);
            }
        });
    }

    private void setupAdapter(ArrayList<ActividadResponse> list) {
        ActividadesApiAdapter.OnItemClickListener listener = (item, position) -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("actividad", item);
            intent.putExtra("IS_ORG_VIEW", true);
            startActivity(intent);
        };

        ActividadesApiAdapter.OnItemClickListener editListener = (item, position) -> {
            // Solo permitir editar actividades en revisión
            String estado = item.getEstadoPublicacion();
            if (estado != null && estado.equalsIgnoreCase("En revision")) {
                Intent intent = new Intent(getContext(), EditActividad.class);
                intent.putExtra("actividad", item);
                startActivity(intent);
            } else {
                android.widget.Toast.makeText(getContext(),
                        "Solo se pueden editar actividades en revisión",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        };

        adapter = new ActividadesApiAdapter(list, listener, editListener, true);
        rvActivities.setAdapter(adapter);
    }

    private void setupFilters() {
        List<String> statusOptions = Arrays.asList("Todas", "Publicada", "En revision", "Finalizada", "Cancelada");

        FilterAdapter filterAdapter = new FilterAdapter(statusOptions, selectedStatus -> {
            currentStatusFilter = selectedStatus;
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
        ArrayList<ActividadResponse> filtered = new ArrayList<>();

        for (ActividadResponse item : masterList) {
            boolean matchesSearch = item.getTitulo() != null &&
                    item.getTitulo().toLowerCase().contains(currentSearchText);

            boolean matchesStatus = currentStatusFilter.equals("Todas");
            if (!matchesStatus && item.getEstadoPublicacion() != null) {
                matchesStatus = item.getEstadoPublicacion().equalsIgnoreCase(currentStatusFilter);
            }

            if (matchesSearch && matchesStatus) {
                filtered.add(item);
            }
        }

        setupAdapter(filtered);
        checkEmptyState(filtered.size());
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