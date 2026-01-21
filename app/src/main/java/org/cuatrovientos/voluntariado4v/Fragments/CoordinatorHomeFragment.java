package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.API.VoluntariadoApiService;
import org.cuatrovientos.voluntariado4v.Activities.CoordinatorDashboard;
import org.cuatrovientos.voluntariado4v.Models.CoordinatorStatsResponse;
import org.cuatrovientos.voluntariado4v.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorHomeFragment extends Fragment {

    // Cabecera
    private TextView tvWelcomeTitle, tvWelcomeSubtitle;
    private ImageView ivUserAvatar;

    // Contenedores interactivos (Clicks)
    private LinearLayout layoutPendingVolunteers, layoutPendingActivities;

    // Contadores
    private TextView tvPendingVolunteers, tvPendingActivities;
    private TextView tvTotalVolunteers, tvTotalOrganizations, tvTotalActivities;

    private VoluntariadoApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_home, container, false);

        initViews(view);
        setupUserHeader();
        setupClickListeners(); // <--- Nueva funcionalidad: Navegación

        try {
            apiService = ApiClient.getService();
            loadStats();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error de conexión con API", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void initViews(View view) {
        tvWelcomeTitle = view.findViewById(R.id.tvWelcomeTitle);
        tvWelcomeSubtitle = view.findViewById(R.id.tvWelcomeSubtitle);
        ivUserAvatar = view.findViewById(R.id.ivUserAvatar);

        // Importante: En el XML, estos LinearLayouts son los contenedores de las columnas "Pendientes"
        // Necesitamos asignarles IDs en el XML si no los tienen, o usar el padre.
        // TRUCO: Como en el XML anterior no les pusimos ID a los LinearLayout internos,
        // vamos a buscar los TextViews y asignar el click listener a su padre directo.

        tvPendingVolunteers = view.findViewById(R.id.tvPendingVolunteers);
        tvPendingActivities = view.findViewById(R.id.tvPendingActivities);

        // Obtenemos los padres para hacerlos clickeables (Área de toque más grande)
        layoutPendingVolunteers = (LinearLayout) tvPendingVolunteers.getParent();
        layoutPendingActivities = (LinearLayout) tvPendingActivities.getParent();

        tvTotalVolunteers = view.findViewById(R.id.tvTotalVolunteers);
        tvTotalOrganizations = view.findViewById(R.id.tvTotalOrganizations);
        tvTotalActivities = view.findViewById(R.id.tvTotalActivities);
    }

    private void setupClickListeners() {
        // Al hacer clic en VOLUNTARIOS PENDIENTES -> Ir a Pestaña Usuarios
        layoutPendingVolunteers.setOnClickListener(v -> {
            if (getActivity() instanceof CoordinatorDashboard) {
                ((CoordinatorDashboard) getActivity()).switchToTab(R.id.navigation_users);
            }
        });

        // Al hacer clic en ACTIVIDADES PENDIENTES -> Ir a Pestaña Actividades
        layoutPendingActivities.setOnClickListener(v -> {
            if (getActivity() instanceof CoordinatorDashboard) {
                ((CoordinatorDashboard) getActivity()).switchToTab(R.id.navigation_activities);
            }
        });
    }

    private void setupUserHeader() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Coordinador");

        tvWelcomeTitle.setText("Hola, " + userName);
        tvWelcomeSubtitle.setText("Panel de Control");
        ivUserAvatar.setImageResource(R.drawable.nouser);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (apiService != null) loadStats();
    }

    private void loadStats() {
        Call<CoordinatorStatsResponse> call = apiService.getCoordinatorStats();
        call.enqueue(new Callback<CoordinatorStatsResponse>() {
            @Override
            public void onResponse(Call<CoordinatorStatsResponse> call, Response<CoordinatorStatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    setEmptyStats();
                }
            }

            @Override
            public void onFailure(Call<CoordinatorStatsResponse> call, Throwable t) {
                setEmptyStats();
            }
        });
    }

    private void updateUI(CoordinatorStatsResponse stats) {
        if (tvPendingVolunteers == null) return;

        tvPendingVolunteers.setText(String.valueOf(stats.getPendingVolunteerRequests()));
        tvPendingActivities.setText(String.valueOf(stats.getPendingActivityRequests()));
        tvTotalVolunteers.setText(String.valueOf(stats.getTotalVolunteers()));
        tvTotalOrganizations.setText(String.valueOf(stats.getTotalOrganizations()));
        tvTotalActivities.setText(String.valueOf(stats.getTotalActivities()));
    }

    private void setEmptyStats() {
        if (tvPendingVolunteers == null) return;
        tvPendingVolunteers.setText("0");
        tvPendingActivities.setText("0");
        tvTotalVolunteers.setText("-");
        tvTotalOrganizations.setText("-");
        tvTotalActivities.setText("-");
    }
}