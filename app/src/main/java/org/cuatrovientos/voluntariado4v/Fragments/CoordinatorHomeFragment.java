package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // O View si usas otro elemento
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Activities.ManageOdsActivity;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.CoordinadorResponse;
import org.cuatrovientos.voluntariado4v.Models.CoordinatorStatsResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorHomeFragment extends Fragment {

    // Vistas Principales
    private TextView tvWelcome, tvStatusHeader;
    private ImageView ivStatusIcon;

    // Contadores
    private TextView tvPendingVolunteers, tvPendingActivities;
    private TextView tvTotalVolunteers, tvTotalOrganizations, tvTotalActivities;

    // NUEVO: Botones de Gestión
    private View btnManageOds;
    private View btnManageTypes;

    // Datos y Estado
    private Context context;
    private int currentUserId;
    private int pendingVolunteersCount = 0;
    private int pendingActivitiesCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_home, container, false);
        context = requireContext();

        SharedPreferences prefs = context.getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        initViews(view);

        // Configurar navegación al hacer clic en los contadores
        setupDashboardLinks();

        // NUEVO: Configurar botones de gestión
        setupManagementButtons();

        if (currentUserId != -1) {
            loadUserProfile();
            loadDashboardStats();           // Carga voluntarios pendientes y totales
            countPendingActivitiesManually(); // Carga actividades pendientes reales
        } else {
            setEmptyStats();
        }

        return view;
    }

    private void initViews(View view) {
        // Encabezado de Estado
        tvWelcome = view.findViewById(R.id.tvWelcomeTitle);
        tvStatusHeader = view.findViewById(R.id.tvStatusHeader);
        ivStatusIcon = view.findViewById(R.id.ivStatusIcon);

        // Tarjetas Grandes (Pendientes)
        tvPendingVolunteers = view.findViewById(R.id.tvPendingVolunteers);
        tvPendingActivities = view.findViewById(R.id.tvPendingActivities);

        // Tarjetas Pequeñas (Totales)
        tvTotalVolunteers = view.findViewById(R.id.tvTotalVolunteers);
        tvTotalOrganizations = view.findViewById(R.id.tvTotalOrganizations);
        tvTotalActivities = view.findViewById(R.id.tvTotalActivities);

        // NUEVO: Botones
        // Asegúrate de que los IDs en tu XML sean 'btnManageOds' y 'btnManageTypes'
        btnManageOds = view.findViewById(R.id.btnManageOds);
        btnManageTypes = view.findViewById(R.id.btnManageTypes);
    }

    /**
     * Configura los clics en las secciones de pendientes para navegar
     * a sus respectivas pestañas.
     */
    private void setupDashboardLinks() {
        // Clic en zona "Voluntarios pendientes" -> Ir a Pestaña Usuarios
        if (tvPendingVolunteers != null) {
            // Obtenemos el contenedor padre (LinearLayout) para hacer clicable toda la zona
            View containerVolunteers = (View) tvPendingVolunteers.getParent();
            if (containerVolunteers != null) {
                containerVolunteers.setOnClickListener(v -> navigateToTab(R.id.navigation_users));
            }
        }

        // Clic en zona "Actividades pendientes" -> Ir a Pestaña Actividades
        if (tvPendingActivities != null) {
            // Obtenemos el contenedor padre (LinearLayout) para hacer clicable toda la zona
            View containerActivities = (View) tvPendingActivities.getParent();
            if (containerActivities != null) {
                containerActivities.setOnClickListener(v -> navigateToTab(R.id.navigation_activities));
            }
        }
    }

    /**
     * NUEVO: Configura la navegación a las pantallas de gestión
     */
    private void setupManagementButtons() {
        // Botón Gestionar ODS
        if (btnManageOds != null) {
            btnManageOds.setOnClickListener(v -> {
                Intent intent = new Intent(context, ManageOdsActivity.class);
                startActivity(intent);
            });
        }

        // Botón Gestionar Tipos
        if (btnManageTypes != null) {
            btnManageTypes.setOnClickListener(v -> {
                // AHORA SÍ: Abrimos la nueva Activity
                Intent intent = new Intent(context, org.cuatrovientos.voluntariado4v.Activities.ManageTiposActivity.class);
                startActivity(intent);
            });
        }
    }

    /**
     * Metodo auxiliar para cambiar la pestaña del BottomNavigationView
     */
    private void navigateToTab(int menuItemId) {
        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_view_coordinator);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(menuItemId);
            }
        }
    }

    // 1. CARGAR NOMBRE
    private void loadUserProfile() {
        ApiClient.getService().getCoordinadorDetail(currentUserId, currentUserId).enqueue(new Callback<CoordinadorResponse>() {
            @Override
            public void onResponse(Call<CoordinadorResponse> call, Response<CoordinadorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String nombre = response.body().getNombre();
                    if (tvWelcome != null) {
                        tvWelcome.setText("Hola, " + (nombre != null && !nombre.isEmpty() ? nombre : "Coordinador"));
                    }
                }
            }
            @Override public void onFailure(Call<CoordinadorResponse> call, Throwable t) { }
        });
    }

    // 2. CARGAR ESTADÍSTICAS GENERALES (Voluntarios + Totales)
    private void loadDashboardStats() {
        ApiClient.getService().getCoordinatorStats(currentUserId).enqueue(new Callback<CoordinatorStatsResponse>() {
            @Override
            public void onResponse(Call<CoordinatorStatsResponse> call, Response<CoordinatorStatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CoordinatorStatsResponse.Metricas stats = response.body().getMetricas();

                    // Guardamos el dato de voluntarios pendientes
                    pendingVolunteersCount = stats.pendingVolunteerRequests;

                    // Actualizamos UI
                    if (tvPendingVolunteers != null) tvPendingVolunteers.setText(String.valueOf(pendingVolunteersCount));
                    if (tvTotalVolunteers != null) tvTotalVolunteers.setText(String.valueOf(stats.totalVolunteers));
                    if (tvTotalOrganizations != null) tvTotalOrganizations.setText(String.valueOf(stats.totalOrganizations));
                    if (tvTotalActivities != null) tvTotalActivities.setText(String.valueOf(stats.totalActivities));

                    // Refrescamos el mensaje de cabecera
                    refreshStatusHeader();
                }
            }
            @Override public void onFailure(Call<CoordinatorStatsResponse> call, Throwable t) { }
        });
    }

    // 3. CARGAR ACTIVIDADES PENDIENTES (Manual y Preciso)
    private void countPendingActivitiesManually() {
        ApiClient.getService().getAllActivitiesCoord(currentUserId).enqueue(new Callback<List<ActividadResponse>>() {
            @Override
            public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int conteo = 0;
                    for (ActividadResponse act : response.body()) {
                        String estado = act.getEstadoPublicacion();
                        if (estado != null) {
                            String e = estado.toLowerCase();
                            // Contamos como pendientes: "revision", "pendiente", "solicitada"
                            if (e.contains("revis") || e.contains("pend") || e.contains("solicit")) {
                                conteo++;
                            }
                        }
                    }

                    // Guardamos el dato real
                    pendingActivitiesCount = conteo;

                    if (tvPendingActivities != null) {
                        tvPendingActivities.setText(String.valueOf(pendingActivitiesCount));
                    }

                    // Refrescamos el mensaje de cabecera
                    refreshStatusHeader();
                }
            }
            @Override public void onFailure(Call<List<ActividadResponse>> call, Throwable t) { }
        });
    }

    // 4. LÓGICA DEL MENSAJE DE ESTADO
    private void refreshStatusHeader() {
        if (tvStatusHeader == null || ivStatusIcon == null || getContext() == null) return;

        boolean hayPendientes = (pendingVolunteersCount > 0 || pendingActivitiesCount > 0);

        if (hayPendientes) {
            // CASO: HAY TRABAJO PENDIENTE
            tvStatusHeader.setText("ATENCIÓN REQUERIDA");
            tvStatusHeader.setTextColor(Color.parseColor("#E65100")); // Naranja oscuro / Rojo
            ivStatusIcon.setImageResource(R.drawable.warning);
            ivStatusIcon.setColorFilter(Color.parseColor("#E65100"));
        } else {
            // CASO: TODO LIMPIO
            tvStatusHeader.setText("TODO ESTÁ AL DÍA");
            tvStatusHeader.setTextColor(Color.parseColor("#2E7D32")); // Verde
            ivStatusIcon.setImageResource(R.drawable.ic_check_circle);
            ivStatusIcon.setColorFilter(Color.parseColor("#2E7D32"));
        }
    }

    private void setEmptyStats() {
        if (tvPendingVolunteers == null) return;
        tvPendingVolunteers.setText("0");
        tvPendingActivities.setText("0");
        tvTotalVolunteers.setText("0");
        tvTotalOrganizations.setText("0");
        tvTotalActivities.setText("0");
    }
}