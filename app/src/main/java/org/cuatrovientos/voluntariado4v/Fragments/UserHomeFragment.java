package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.cuatrovientos.voluntariado4v.API.ApiClient;
import org.cuatrovientos.voluntariado4v.Adapters.DashboardOrganizationsAdapter;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.HistorialApiResponse;
import org.cuatrovientos.voluntariado4v.Models.TopOrganizacionResponse;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioResponse;
import org.cuatrovientos.voluntariado4v.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeFragment extends Fragment {

    private TextView tvName, tvGreeting;
    // Próxima actividad
    private MaterialCardView cardActivity;
    private TextView tvActivityTitle, tvDateMonth, tvDateDay, tvTime, tvLocation, tvOrgName;
    private TextView tvNextActivityLabel;

    // Top Organizaciones
    private RecyclerView rvTopOrganizations;

    public UserHomeFragment() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadUserData();
        loadNextActivity();
        loadTopOrganizations();
    }

    private void initViews(View view) {
        tvName = view.findViewById(R.id.tvName);
        tvGreeting = view.findViewById(R.id.tvGreeting);

        // Próxima actividad UI
        cardActivity = view.findViewById(R.id.cardActivity);
        tvNextActivityLabel = view.findViewById(R.id.tvNextActivityLabel);
        tvActivityTitle = view.findViewById(R.id.tvActivityTitle);
        tvDateMonth = view.findViewById(R.id.tvDateMonth);
        tvDateDay = view.findViewById(R.id.tvDateDay);
        tvTime = view.findViewById(R.id.tvTime);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvOrgName = view.findViewById(R.id.tvOrgName);

        // Top Organizations
        rvTopOrganizations = view.findViewById(R.id.rvTopOrganizations);
        if (rvTopOrganizations != null) {
            rvTopOrganizations.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private int getUserId() {
        if (getContext() == null)
            return -1;
        SharedPreferences prefs = getContext().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void loadUserData() {
        int userId = getUserId();
        if (userId == -1)
            return;

        ApiClient.getService().getVoluntario(userId).enqueue(new Callback<VoluntarioResponse>() {
            @Override
            public void onResponse(Call<VoluntarioResponse> call, Response<VoluntarioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VoluntarioResponse vol = response.body();
                    tvName.setText(vol.getNombre() + " " + vol.getApellidos());
                }
            }

            @Override
            public void onFailure(Call<VoluntarioResponse> call, Throwable t) {
            }
        });
    }

    private void loadNextActivity() {
        int userId = getUserId();
        if (userId == -1)
            return;

        // Ocultar por defecto hasta cargar
        if (cardActivity != null)
            cardActivity.setVisibility(View.GONE);
        if (tvNextActivityLabel != null)
            tvNextActivityLabel.setVisibility(View.GONE);

        ApiClient.getService().getHistorial(userId, userId).enqueue(new Callback<HistorialApiResponse>() {
            @Override
            public void onResponse(Call<HistorialApiResponse> call, Response<HistorialApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getActividades() != null) {
                    HistorialApiResponse.InscripcionItem nextActivity = null;
                    Date now = new Date();
                    Date closestDate = null;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    if (response.body().getActividades().isEmpty())
                        return;

                    for (HistorialApiResponse.InscripcionItem item : response.body().getActividades()) {
                        try {
                            // Limpiar fecha
                            String cleanDateStr = item.toActividadResponse().getFechaInicio().replace("T", " ");
                            if (cleanDateStr.length() > 19)
                                cleanDateStr = cleanDateStr.substring(0, 19);

                            Date itemDate = sdf.parse(cleanDateStr);

                            // Si es futura (o hoy) y estado es aceptada/confirmada (o pendiente si se
                            // desea)
                            String estado = item.getEstadoInscripcion();
                            if (itemDate != null && itemDate.after(now) &&
                                    ("Aceptada".equalsIgnoreCase(estado) || "Confirmada".equalsIgnoreCase(estado)
                                            || "Pendiente".equalsIgnoreCase(estado))) {

                                if (closestDate == null || itemDate.before(closestDate)) {
                                    closestDate = itemDate;
                                    nextActivity = item;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (nextActivity != null && closestDate != null) {
                        displayNextActivity(nextActivity, closestDate);
                    } else {
                        // No hay próxima actividad
                        if (tvNextActivityLabel != null) {
                            tvNextActivityLabel.setText("Sin próximas actividades");
                            tvNextActivityLabel.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HistorialApiResponse> call, Throwable t) {
            }
        });
    }

    private void displayNextActivity(HistorialApiResponse.InscripcionItem item, Date date) {
        if (cardActivity == null)
            return;

        cardActivity.setVisibility(View.VISIBLE);
        if (tvNextActivityLabel != null)
            tvNextActivityLabel.setVisibility(View.VISIBLE);

        ActividadResponse act = item.toActividadResponse();

        if (tvActivityTitle != null)
            tvActivityTitle.setText(act.getTitulo());
        if (tvLocation != null)
            tvLocation.setText(act.getUbicacion());
        if (tvOrgName != null)
            tvOrgName.setText(act.getNombreOrganizacion());

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", new Locale("es", "ES"));
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (tvDateMonth != null)
            tvDateMonth.setText(monthFormat.format(date).toUpperCase());
        if (tvDateDay != null)
            tvDateDay.setText(dayFormat.format(date));

        String startTime = timeFormat.format(date);
        if (tvTime != null) {
            tvTime.setText(startTime + " (" + act.getDuracionHoras() + "h)");
        }

        // Listener para abrir detalle
        cardActivity.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getContext(),
                    org.cuatrovientos.voluntariado4v.Activities.DetailActivity.class);
            intent.putExtra("actividad", item.toActividadResponse());
            intent.putExtra("INSCRIPCION_STATUS", item.getEstadoInscripcion());
            startActivity(intent);
        });
    }

    private void loadTopOrganizations() {
        ApiClient.getService().getTopOrganizaciones().enqueue(new Callback<List<TopOrganizacionResponse>>() {
            @Override
            public void onResponse(Call<List<TopOrganizacionResponse>> call,
                    Response<List<TopOrganizacionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TopOrganizacionResponse> topOrgs = response.body();
                    // Limitar a 3 si vienen más
                    if (topOrgs.size() > 3) {
                        topOrgs = topOrgs.subList(0, 3);
                    }
                    DashboardOrganizationsAdapter adapter = new DashboardOrganizationsAdapter(topOrgs);
                    rvTopOrganizations.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<TopOrganizacionResponse>> call, Throwable t) {
                // Fail silently
            }
        });
    }
}