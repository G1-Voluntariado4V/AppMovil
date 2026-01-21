package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.API.VoluntariadoApiService;
import org.cuatrovientos.voluntariado4v.Models.CoordinatorStatsResponse;
import org.cuatrovientos.voluntariado4v.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorHomeFragment extends Fragment {

    private TextView tvPendingVolunteers, tvPendingActivities;
    private TextView tvTotalVolunteers, tvTotalOrganizations, tvTotalActivities;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_home, container, false);
        context = requireContext();

        // 1. Vincular Vistas
        tvPendingVolunteers = view.findViewById(R.id.tvPendingVolunteers);
        tvPendingActivities = view.findViewById(R.id.tvPendingActivities);
        tvTotalVolunteers = view.findViewById(R.id.tvTotalVolunteers);
        tvTotalOrganizations = view.findViewById(R.id.tvTotalOrganizations);
        tvTotalActivities = view.findViewById(R.id.tvTotalActivities);

        // 2. Cargar Datos
        loadDashboardStats();

        return view;
    }

    private void loadDashboardStats() {
        SharedPreferences prefs = context.getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        int adminId = prefs.getInt("user_id", -1);

        if (adminId == -1) {
            setEmptyStats();
            return;
        }

        VoluntariadoApiService service = ApiClient.getService();
        service.getCoordinatorStats(adminId).enqueue(new Callback<CoordinatorStatsResponse>() {
            @Override
            public void onResponse(Call<CoordinatorStatsResponse> call, Response<CoordinatorStatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Dashboard", "Datos recibidos correctamente");
                    updateUI(response.body());
                } else {
                    Log.e("Dashboard", "Error API: " + response.code());
                    setEmptyStats();
                    // Opcional: mostrar error solo si no es 403/401
                    if (response.code() >= 500) {
                        Toast.makeText(context, "Error servidor", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CoordinatorStatsResponse> call, Throwable t) {
                Log.e("Dashboard", "Fallo red: " + t.getMessage());
                setEmptyStats();
            }
        });
    }

    private void updateUI(CoordinatorStatsResponse response) {
        if (tvPendingVolunteers == null || getContext() == null) return;

        CoordinatorStatsResponse.Metricas stats = response.getMetricas();

        // Asignamos valores a las tarjetas grandes (Pendientes)
        tvPendingVolunteers.setText(String.valueOf(stats.pendingVolunteerRequests));
        tvPendingActivities.setText(String.valueOf(stats.pendingActivityRequests));

        // Asignamos valores a las tarjetas pequeñas (Totales)
        tvTotalVolunteers.setText(String.valueOf(stats.totalVolunteers));
        tvTotalOrganizations.setText(String.valueOf(stats.totalOrganizations));
        tvTotalActivities.setText(String.valueOf(stats.totalActivities));
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