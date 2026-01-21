package org.cuatrovientos.voluntariado4v.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.cuatrovientos.voluntariado4v.Activities.CreateActividad;
import org.cuatrovientos.voluntariado4v.Activities.DetailActivity;
import org.cuatrovientos.voluntariado4v.Adapters.ActividadesApiAdapter;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.OrganizacionResponse;
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.API.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizationHomeFragment extends Fragment {

    private TextView tvName, tvActive, tvVols;
    private ImageView ivLogo;
    private RecyclerView rv;
    private ActividadesApiAdapter adapter;
    private int orgId;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_organization_home, container, false);

        // 1. Obtener ID de Organización de SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("VoluntariadoPrefs", Context.MODE_PRIVATE);
        orgId = prefs.getInt("user_id", -1);

        // 2. Vincular vistas
        tvName = root.findViewById(R.id.tvOrgWelcome);
        ivLogo = root.findViewById(R.id.ivOrgLogoHeader);
        tvActive = root.findViewById(R.id.tvStatsActive);
        tvVols = root.findViewById(R.id.tvStatsVolunteers);
        Button btnCreate = root.findViewById(R.id.btnCreateActivity);
        View btnEdit = root.findViewById(R.id.btnEditProfile);
        rv = root.findViewById(R.id.rvActiveActivities);

        // 3. Configurar RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActividadesApiAdapter(new ArrayList<>(), (actividad, position) -> {
            // AL CLICAR UNA ACTIVIDAD
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("actividad", actividad); // Pasamos el objeto completo Serializable
            intent.putExtra("IS_ORG_VIEW", true); // IMPORTANTE: Indicamos que lo ve la organización
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        // 4. Cargar Datos de API
        if (orgId != -1) {
            fetchOrganizationProfile();
            fetchOrganizationActivities();
        } else {
            Toast.makeText(getContext(), "Error de sesión", Toast.LENGTH_SHORT).show();
        }

        // 5. Listeners Botones
        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(),
                    CreateActividad.class);
            startActivity(intent);
        });

        return root;
    }

    private void fetchOrganizationProfile() {
        ApiClient.getService().getOrganizationDetail(orgId).enqueue(new Callback<OrganizacionResponse>() {
            @Override
            public void onResponse(Call<OrganizacionResponse> call, Response<OrganizacionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrganizacionResponse org = response.body();
                    tvName.setText(org.getNombre());
                    // Cargar imagen si tienes Glide o usar placeholder
                    // Glide.with(getContext()).load(org.getFotoPerfil()).into(ivLogo);
                    ivLogo.setImageResource(R.drawable.squarelogo); // Placeholder seguro
                }
            }

            @Override
            public void onFailure(Call<OrganizacionResponse> call, Throwable t) {
                Log.e("OrgHome", "Error perfil: " + t.getMessage());
            }
        });
    }

    private void fetchOrganizationActivities() {
        ApiClient.getService().getActividadesOrganizacion(orgId).enqueue(new Callback<List<ActividadResponse>>() {
            @Override
            public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ActividadResponse> actividades = response.body();
                    adapter.updateData(actividades);

                    // Actualizar contador de actividades
                    tvActive.setText(String.valueOf(actividades.size()));

                    // Calcular total de voluntarios sumando inscritos de todas las actividades
                    int totalVoluntarios = 0;
                    for (ActividadResponse act : actividades) {
                        totalVoluntarios += act.getInscritosConfirmados();
                    }
                    tvVols.setText(String.valueOf(totalVoluntarios));
                }
            }

            @Override
            public void onFailure(Call<List<ActividadResponse>> call, Throwable t) {
                Log.e("OrgHome", "Error actividades: " + t.getMessage());
            }
        });
    }
}