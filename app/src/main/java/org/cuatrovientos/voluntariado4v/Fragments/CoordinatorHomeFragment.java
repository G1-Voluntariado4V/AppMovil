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

    // Contenedores interactivos
    private LinearLayout layoutPendingVolunteers, layoutPendingActivities;

    // Contadores
    private TextView tvPendingVolunteers, tvPendingActivities;
    private TextView tvTotalVolunteers, tvTotalOrganizations, tvTotalActivities;

    private VoluntariadoApiService apiService;
    private int currentAdminId; // ID del coordinador

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_home, container, false);

        initViews(view);
        setupUserHeader(); // Aquí recuperamos el ID
        setupClickListeners();

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

        tvPendingVolunteers = view.findViewById(R.id.tvPendingVolunteers);
        tvPendingActivities = view.findViewById(R.id.tvPendingActivities);

        layoutPendingVolunteers = (LinearLayout) tvPendingVolunteers.getParent();
        layoutPendingActivities = (LinearLayout) tvPendingActivities.getParent();

        tvTotalVolunteers = view.findViewById(R.id.tvTotalVolunteers);
        tvTotalOrganizations = view.findViewById(R.id.tvTotalOrganizations);
        tvTotalActivities = view.findViewById(R.id.tvTotalActivities);
    }

    private void setupClickListeners() {
        layoutPendingVolunteers.setOnClickListener(v -> {
            if (getActivity() instanceof CoordinatorDashboard) {
                ((CoordinatorDashboard) getActivity()).switchToTab(R.id.navigation_users);
            }
        });

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
        currentAdminId = prefs.getInt("user_id", -1); // <-- Recuperar ID

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
        // CORRECCIÓN: Pasar el ID del admin
        Call<CoordinatorStatsResponse> call = apiService.getCoordinatorStats(currentAdminId);

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

    private void updateUI(CoordinatorStatsResponse response) {
        if (tvPendingVolunteers == null) return;

        // CORRECCIÓN: Acceder al objeto interno "metricas"
        CoordinatorStatsResponse.Metricas stats = response.getMetricas();

        if (stats != null) {
            tvPendingVolunteers.setText(String.valueOf(stats.pendingVolunteerRequests));
            tvPendingActivities.setText(String.valueOf(stats.pendingActivityRequests));
            tvTotalVolunteers.setText(String.valueOf(stats.totalVolunteers));
            tvTotalOrganizations.setText(String.valueOf(stats.totalOrganizations));
            tvTotalActivities.setText(String.valueOf(stats.totalActivities));
        } else {
            setEmptyStats();
        }
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