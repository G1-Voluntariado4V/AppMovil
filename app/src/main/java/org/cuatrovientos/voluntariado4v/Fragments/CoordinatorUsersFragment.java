package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Context;
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
import org.cuatrovientos.voluntariado4v.Adapters.CoordinatorUsersAdapter;
import org.cuatrovientos.voluntariado4v.Adapters.FilterAdapter;
import org.cuatrovientos.voluntariado4v.Models.EstadoRequest;
import org.cuatrovientos.voluntariado4v.Models.MensajeResponse;
import org.cuatrovientos.voluntariado4v.Models.UserResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorUsersFragment extends Fragment implements CoordinatorUsersAdapter.OnUserActionListener {

    // Vistas
    private TabLayout tabLayout;
    private RecyclerView rvPending, rvAllUsers, rvFilters;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private EditText etSearch;

    // Adaptadores
    private CoordinatorUsersAdapter pendingAdapter;
    private CoordinatorUsersAdapter allUsersAdapter;
    private FilterAdapter filterAdapter;

    // Datos
    private VoluntariadoApiService apiService;
    private List<UserResponse> masterList = new ArrayList<>(); // Lista completa descargada
    private int currentAdminId; // ID del coordinador logueado

    // Estado filtros
    private String currentSearchText = "";
    private String currentRoleFilter = "Todos";
    private int currentTabPosition = 0; // 0 = Directorio, 1 = Solicitudes

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_users, container, false);

        // Recuperar ID del Coordinador
        SharedPreferences prefs = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentAdminId = prefs.getInt("user_id", -1);

        initViews(view);
        setupAdapters();
        setupFilters();
        setupSearch();
        setupTabs(); // Configura pestañas y visibilidad inicial

        try {
            apiService = ApiClient.getService();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayoutUsers);
        rvPending = view.findViewById(R.id.rvPendingRequests);
        rvAllUsers = view.findViewById(R.id.rvAllUsers);
        rvFilters = view.findViewById(R.id.rvFilters);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        etSearch = view.findViewById(R.id.etSearchUser);
    }

    private void setupAdapters() {
        pendingAdapter = new CoordinatorUsersAdapter(getContext(), CoordinatorUsersAdapter.TYPE_PENDING, this);
        rvPending.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPending.setAdapter(pendingAdapter);

        allUsersAdapter = new CoordinatorUsersAdapter(getContext(), CoordinatorUsersAdapter.TYPE_ALL_USERS, this);
        rvAllUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAllUsers.setAdapter(allUsersAdapter);
    }

    private void setupFilters() {
        rvFilters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<String> roles = Arrays.asList("Todos", "Voluntario", "Organización", "Coordinador");

        filterAdapter = new FilterAdapter(roles, category -> {
            currentRoleFilter = category;
            applyFilters();
        });
        rvFilters.setAdapter(filterAdapter);
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
        // Orden: Directorio (0) -> Solicitudes (1)
        tabLayout.addTab(tabLayout.newTab().setText("Directorio"));
        tabLayout.addTab(tabLayout.newTab().setText("Solicitudes"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                updateVisibility();
                applyFilters();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // CORRECCIÓN: Forzar actualización de visibilidad inicial (para mostrar Directorio y ocultar Solicitudes)
        // Ya que el XML por defecto podría tener rvPending visible y rvAllUsers gone.
        updateVisibility();
    }

    private void updateVisibility() {
        rvFilters.setVisibility(View.VISIBLE);

        if (currentTabPosition == 0) {
            // Pestaña 0: Directorio (Mostrar lista usuarios, ocultar pendientes)
            rvPending.setVisibility(View.GONE);
            rvAllUsers.setVisibility(View.VISIBLE);
        } else {
            // Pestaña 1: Solicitudes
            rvPending.setVisibility(View.VISIBLE);
            rvAllUsers.setVisibility(View.GONE);
        }
    }

    // ---------------- API Calls ----------------

    private void loadData() {
        showLoading(true);
        apiService.getAllUsers().enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    masterList = response.body();
                    applyFilters(); // Aplica filtros y actualiza el adaptador correcto
                } else {
                    toggleEmptyState(true);
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                showLoading(false);
                toggleEmptyState(true);
            }
        });
    }

    private void applyFilters() {
        List<UserResponse> pendingList = new ArrayList<>();
        List<UserResponse> activeList = new ArrayList<>();

        for (UserResponse user : masterList) {
            // 1. Buscador
            String nombre = user.getNombre() != null ? user.getNombre().toLowerCase() : "";
            String correo = user.getCorreo() != null ? user.getCorreo().toLowerCase() : "";

            if (!correo.contains(currentSearchText) && !nombre.contains(currentSearchText)) {
                continue;
            }

            // 2. Filtro de Rol
            boolean matchesRole = true;
            if (!currentRoleFilter.equals("Todos")) {
                String userRol = user.getRol().toLowerCase();
                String filterRol = currentRoleFilter.toLowerCase();

                if (filterRol.startsWith("org") && !userRol.startsWith("org")) matchesRole = false;
                if (filterRol.startsWith("vol") && !userRol.startsWith("vol")) matchesRole = false;
                if (filterRol.startsWith("coor") && !userRol.startsWith("coor")) matchesRole = false;
            }

            if (!matchesRole) continue;

            // 3. Separación por Estado
            String estado = user.getEstadoCuenta();
            if ("Pendiente".equalsIgnoreCase(estado)) {
                pendingList.add(user);
            } else {
                activeList.add(user);
            }
        }

        // Actualizar la lista visible según la pestaña actual
        if (currentTabPosition == 0) {
            // Pestaña Directorio
            allUsersAdapter.setUsersList(activeList);
            toggleEmptyState(activeList.isEmpty());
        } else {
            // Pestaña Solicitudes
            pendingAdapter.setUsersList(pendingList);
            toggleEmptyState(pendingList.isEmpty());
        }
    }

    // ---------------- Acciones ----------------

    @Override
    public void onApprove(int id) {
        changeStatus(id, "Activa");
    }

    @Override
    public void onReject(int id) {
        changeStatus(id, "Rechazada");
    }

    @Override
    public void onEditRole(UserResponse user) {
        Toast.makeText(getContext(), "Edición de rol no habilitada en API", Toast.LENGTH_SHORT).show();
    }

    private void changeStatus(int userId, String nuevoEstado) {
        String rolPath = "voluntarios"; // Por defecto
        for (UserResponse u : masterList) {
            if (u.getId() == userId) {
                if (u.getRol().toLowerCase().startsWith("org")) {
                    rolPath = "organizaciones";
                }
                break;
            }
        }

        EstadoRequest body = new EstadoRequest(nuevoEstado);

        apiService.updateUserStatus(currentAdminId, rolPath, userId, body).enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Usuario " + nuevoEstado, Toast.LENGTH_SHORT).show();
                    loadData(); // Recargar datos
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable t) {
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