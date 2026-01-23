package org.cuatrovientos.voluntariado4v.Fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.API.VoluntariadoApiService;
import org.cuatrovientos.voluntariado4v.Activities.DetailActivity;
import org.cuatrovientos.voluntariado4v.Activities.EditUserActivity;
import org.cuatrovientos.voluntariado4v.Adapters.CoordinatorEnrollmentsSimpleAdapter;
import org.cuatrovientos.voluntariado4v.Adapters.CoordinatorUsersAdapter;
import org.cuatrovientos.voluntariado4v.Adapters.FilterAdapter;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.EstadoRequest;
import org.cuatrovientos.voluntariado4v.Models.MensajeResponse;
import org.cuatrovientos.voluntariado4v.Models.PendingEnrollmentResponse;
import org.cuatrovientos.voluntariado4v.Models.UserResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorUsersFragment extends Fragment implements CoordinatorUsersAdapter.OnUserActionListener,
        CoordinatorEnrollmentsSimpleAdapter.OnEnrollmentActionListener {

    private TabLayout tabLayout;
    private RecyclerView rvPending, rvAllUsers, rvFilters;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private EditText etSearch;

    private CoordinatorUsersAdapter pendingAdapter;
    private CoordinatorUsersAdapter allUsersAdapter;
    private CoordinatorEnrollmentsSimpleAdapter enrollmentsSimpleAdapter;
    private ConcatAdapter concatAdapter;
    private FilterAdapter filterAdapter;

    private VoluntariadoApiService apiService;
    private List<UserResponse> masterList = new ArrayList<>();
    private List<PendingEnrollmentResponse> masterEnrollments = new ArrayList<>();
    private int currentAdminId;

    private String currentSearchText = "";
    private String currentRoleFilter = "Todos";
    private int currentTabPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_users, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        currentAdminId = prefs.getInt("user_id", -1);

        initViews(view);
        setupAdapters();
        setupFilters();
        setupSearch();
        setupTabs();

        try {
            apiService = ApiClient.getService();
            loadAllData();
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
        enrollmentsSimpleAdapter = new CoordinatorEnrollmentsSimpleAdapter(getContext(), this);
        concatAdapter = new ConcatAdapter(pendingAdapter, enrollmentsSimpleAdapter);

        rvPending.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPending.setAdapter(concatAdapter);

        allUsersAdapter = new CoordinatorUsersAdapter(getContext(), CoordinatorUsersAdapter.TYPE_ALL_USERS, this);
        rvAllUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAllUsers.setAdapter(allUsersAdapter);
    }

    private void setupFilters() {
        List<String> filters = Arrays.asList("Todos", "Voluntario", "Organización", "Coordinador");
        filterAdapter = new FilterAdapter(filters, filter -> {
            currentRoleFilter = filter;
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

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Directorio"));
        tabLayout.addTab(tabLayout.newTab().setText("Solicitudes"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                updateVisibility();
                applyFilters();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        updateVisibility();
    }

    private void updateVisibility() {
        rvFilters.setVisibility(View.VISIBLE);

        if (currentTabPosition == 0) {
            rvAllUsers.setVisibility(View.VISIBLE);
            rvPending.setVisibility(View.GONE);
        } else {
            rvAllUsers.setVisibility(View.GONE);
            rvPending.setVisibility(View.VISIBLE);
        }
    }

    private void loadAllData() {
        showLoading(true);
        loadUsers(false);
        loadEnrollments(false);
    }

    private void loadUsers(boolean exclusiveLoading) {
        if (exclusiveLoading)
            showLoading(true);
        apiService.getAllUsers().enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (exclusiveLoading)
                    showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    masterList = response.body();
                    applyFilters();
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                if (exclusiveLoading)
                    showLoading(false);
            }
        });
    }

    private void loadEnrollments(boolean exclusiveLoading) {
        if (exclusiveLoading)
            showLoading(true);
        apiService.getPendingEnrollments(currentAdminId).enqueue(new Callback<List<PendingEnrollmentResponse>>() {
            @Override
            public void onResponse(Call<List<PendingEnrollmentResponse>> call,
                    Response<List<PendingEnrollmentResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    masterEnrollments = response.body();
                    applyFilters();
                }
            }

            @Override
            public void onFailure(Call<List<PendingEnrollmentResponse>> call, Throwable t) {
                showLoading(false);
            }
        });
    }

    private void applyFilters() {
        List<UserResponse> activeList = new ArrayList<>();
        List<UserResponse> pendingRegisterList = new ArrayList<>();

        for (UserResponse user : masterList) {
            String nombre = user.getNombre() != null ? user.getNombre().toLowerCase() : "";
            String correo = user.getCorreo() != null ? user.getCorreo().toLowerCase() : "";

            if (!correo.contains(currentSearchText) && !nombre.contains(currentSearchText))
                continue;

            if (!currentRoleFilter.equals("Todos")) {
                String uRol = user.getRol().toLowerCase();
                String fRol = currentRoleFilter.toLowerCase();
                if (fRol.startsWith("org") && !uRol.startsWith("org"))
                    continue;
                if (fRol.startsWith("vol") && !uRol.startsWith("vol"))
                    continue;
                if (fRol.startsWith("coor") && !uRol.startsWith("coor"))
                    continue;
            }

            if ("Pendiente".equalsIgnoreCase(user.getEstadoCuenta())) {
                pendingRegisterList.add(user);
            } else {
                activeList.add(user);
            }
        }

        List<PendingEnrollmentResponse> filteredEnrollments = new ArrayList<>();
        for (PendingEnrollmentResponse item : masterEnrollments) {
            String nombre = (item.getNombreVoluntario() + " " + item.getApellidosVoluntario()).toLowerCase();
            String titulo = item.getTituloActividad().toLowerCase();
            if (nombre.contains(currentSearchText) || titulo.contains(currentSearchText)) {
                filteredEnrollments.add(item);
            }
        }

        if (currentTabPosition == 0) {
            allUsersAdapter.setUsersList(activeList);
            toggleEmptyState(activeList.isEmpty());
        } else {
            pendingAdapter.setUsersList(pendingRegisterList);
            enrollmentsSimpleAdapter.setList(filteredEnrollments);
            boolean empty = pendingRegisterList.isEmpty() && filteredEnrollments.isEmpty();
            toggleEmptyState(empty);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
        if (show) {
            rvAllUsers.setVisibility(View.GONE);
            rvPending.setVisibility(View.GONE);
        } else {
            updateVisibility();
        }
    }

    private void toggleEmptyState(boolean isEmpty) {
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (isEmpty) {
            rvAllUsers.setVisibility(View.GONE);
            rvPending.setVisibility(View.GONE);
        } else {
            updateVisibility();
        }
    }

    @Override
    public void onUserClick(UserResponse user) {
        Intent intent = new Intent(getContext(), EditUserActivity.class);
        intent.putExtra("USER_ID", user.getId());
        intent.putExtra("USER_ROLE", user.getRol());
        startActivity(intent);
    }

    @Override
    public void onApprove(int userId) {
        updateUserStatus(userId, "Activa");
    }

    @Override
    public void onReject(int userId) {
        updateUserStatus(userId, "Rechazada");
    }

    @Override
    public void onEditRole(UserResponse user) {
        onUserClick(user);
    }

    private void updateUserStatus(int userId, String status) {
        showLoading(true);
        EstadoRequest request = new EstadoRequest(status);
        apiService.updateAccountStatus(currentAdminId, userId, request).enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Estado actualizado", Toast.LENGTH_SHORT).show();
                    loadUsers(true);
                } else {
                    showLoading(false);
                }
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable t) {
                showLoading(false);
            }
        });
    }

    @Override
    public void onApprove(PendingEnrollmentResponse item) {
        updateEnrollmentStatus(item, "Aceptada");
    }

    @Override
    public void onReject(PendingEnrollmentResponse item) {
        updateEnrollmentStatus(item, "Rechazada");
    }

    @Override
    public void onViewUser(PendingEnrollmentResponse item) {
        BottomSheetDialog sheet = new BottomSheetDialog(getContext());
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_enrollment_detail, null);
        sheet.setContentView(sheetView);

        TextView tvTitle = sheetView.findViewById(R.id.tvDialogTitle);
        TextView tvAct = sheetView.findViewById(R.id.tvActivityTitle);
        TextView tvName = sheetView.findViewById(R.id.tvVolunteerName);
        TextView tvEmail = sheetView.findViewById(R.id.tvVolunteerEmail);

        if (tvTitle != null)
            tvTitle.setText("Solicitud de Inscripción");
        if (tvAct != null)
            tvAct.setText(item.getTituloActividad());
        if (tvName != null)
            tvName.setText(item.getNombreVoluntario() + " " + item.getApellidosVoluntario());
        if (tvEmail != null)
            tvEmail.setText(item.getEmailVoluntario());

        View cardAct = sheetView.findViewById(R.id.cardActivity);
        if (cardAct != null) {
            cardAct.setOnClickListener(v -> {
                sheet.dismiss();
                fetchAndOpenActivity(item.getIdActividad());
            });
        }

        View cardVol = sheetView.findViewById(R.id.cardVolunteer);
        if (cardVol != null) {
            cardVol.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EditUserActivity.class);
                intent.putExtra("USER_ID", item.getIdVoluntario());
                intent.putExtra("USER_ROLE", "Voluntario");
                startActivity(intent);
                sheet.dismiss();
            });
        }

        View btnApprove = sheetView.findViewById(R.id.btnApprove);
        if (btnApprove != null) {
            btnApprove.setOnClickListener(v -> {
                onApprove(item);
                sheet.dismiss();
            });
        }

        View btnReject = sheetView.findViewById(R.id.btnReject);
        if (btnReject != null) {
            btnReject.setOnClickListener(v -> {
                onReject(item);
                sheet.dismiss();
            });
        }

        sheet.show();
    }

    private void fetchAndOpenActivity(int idActividad) {
        showLoading(true);
        apiService.getActividadDetalle(idActividad).enqueue(new Callback<ActividadResponse>() {
            @Override
            public void onResponse(Call<ActividadResponse> call, Response<ActividadResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("actividad", response.body());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Error al cargar actividad", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ActividadResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEnrollmentStatus(PendingEnrollmentResponse item, String status) {
        showLoading(true);
        EstadoRequest request = new EstadoRequest(status);
        apiService.updateEstadoInscripcion(item.getIdActividad(), item.getIdVoluntario(), request)
                .enqueue(new Callback<MensajeResponse>() {
                    @Override
                    public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Inscripción " + status, Toast.LENGTH_SHORT).show();
                            loadEnrollments(true);
                        } else {
                            showLoading(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<MensajeResponse> call, Throwable t) {
                        showLoading(false);
                    }
                });
    }
}