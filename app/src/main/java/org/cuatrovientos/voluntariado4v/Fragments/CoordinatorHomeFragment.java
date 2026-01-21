package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Models.CoordinadorResponse;
import org.cuatrovientos.voluntariado4v.Models.CoordinatorStatsResponse;
import org.cuatrovientos.voluntariado4v.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorHomeFragment extends Fragment {

    private TextView tvWelcome;
    private TextView tvPendingVolunteers, tvPendingActivities;
    private TextView tvTotalVolunteers, tvTotalOrganizations, tvTotalActivities;

    private Context context;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_home, container, false);
        context = requireContext();

        // Recuperar ID
        SharedPreferences prefs = context.getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        // 1. Vincular Vistas
        // Ajusta "tvWelcome" si en tu XML se llama "tvWelcomeTitle" o diferente
        tvWelcome = view.findViewById(R.id.tvWelcomeTitle);

        tvPendingVolunteers = view.findViewById(R.id.tvPendingVolunteers);
        tvPendingActivities = view.findViewById(R.id.tvPendingActivities);
        tvTotalVolunteers = view.findViewById(R.id.tvTotalVolunteers);
        tvTotalOrganizations = view.findViewById(R.id.tvTotalOrganizations);
        tvTotalActivities = view.findViewById(R.id.tvTotalActivities);

        // 2. Cargar Datos
        if (currentUserId != -1) {
            loadUserProfile();    // Cargar Nombre
            loadDashboardStats(); // Cargar Números
        } else {
            setEmptyStats();
        }

        return view;
    }

    private void loadUserProfile() {
        // Usamos el método getCoordinadorDetail que definimos en la Interfaz
        ApiClient.getService().getCoordinadorDetail(currentUserId, currentUserId).enqueue(new Callback<CoordinadorResponse>() {
            @Override
            public void onResponse(Call<CoordinadorResponse> call, Response<CoordinadorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String nombre = response.body().getNombre();
                    if (tvWelcome != null) {
                        tvWelcome.setText("Hola, " + (nombre.isEmpty() ? "Coordinador" : nombre));
                    }
                }
            }
            @Override
            public void onFailure(Call<CoordinadorResponse> call, Throwable t) {
                Log.e("Home", "Error perfil: " + t.getMessage());
            }
        });
    }

    private void loadDashboardStats() {
        ApiClient.getService().getCoordinatorStats(currentUserId).enqueue(new Callback<CoordinatorStatsResponse>() {
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
        if (getContext() == null) return;

        CoordinatorStatsResponse.Metricas stats = response.getMetricas();

        if (tvPendingVolunteers != null)
            tvPendingVolunteers.setText(String.valueOf(stats.pendingVolunteerRequests));

        if (tvPendingActivities != null)
            tvPendingActivities.setText(String.valueOf(stats.pendingActivityRequests));

        if (tvTotalVolunteers != null)
            tvTotalVolunteers.setText(String.valueOf(stats.totalVolunteers));

        if (tvTotalOrganizations != null)
            tvTotalOrganizations.setText(String.valueOf(stats.totalOrganizations));

        if (tvTotalActivities != null)
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