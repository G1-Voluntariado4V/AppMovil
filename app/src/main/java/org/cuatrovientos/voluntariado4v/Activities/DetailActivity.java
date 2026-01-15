package org.cuatrovientos.voluntariado4v.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.cuatrovientos.voluntariado4v.Adapters.VolunteersAdapter;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioResponse;
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.API.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvOrg, tvLocation, tvDate, tvDesc, tvPlazas, tvCategory;
    private ImageView imgHeader, btnBack;
    private MaterialButton btnJoin, btnOrgProfile;
    private ActividadResponse currentActivity;
    private boolean isOrgView = false; // Flag para saber si es vista de organización

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);

        initViews();

        // Recuperar datos del Intent
        currentActivity = (ActividadResponse) getIntent().getSerializableExtra("actividad");
        isOrgView = getIntent().getBooleanExtra("IS_ORG_VIEW", false);

        if (currentActivity != null) {
            populateData(currentActivity);
            setupMode(); // Configurar modo usuario vs modo organización
        } else {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupListeners();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvMainTitle);
        imgHeader = findViewById(R.id.imgLogoHeader);
        btnBack = findViewById(R.id.btnBack);
        btnJoin = findViewById(R.id.btnJoin);
        tvPlazas = findViewById(R.id.tvPlazasCount);
        tvCategory = findViewById(R.id.tvTagSocial);
        tvDate = findViewById(R.id.tvDetailDate);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvOrg = findViewById(R.id.tvDetailOrg);
        tvDesc = findViewById(R.id.tvDetailDesc);
        btnOrgProfile = findViewById(R.id.btnOrgProfile);
    }

    private void populateData(ActividadResponse activity) {
        if (tvTitle != null) tvTitle.setText(activity.getTitulo());
        if (tvOrg != null) tvOrg.setText(activity.getNombreOrganizacion());
        if (tvLocation != null) tvLocation.setText(activity.getUbicacion());
        if (tvDate != null) tvDate.setText(formatFecha(activity.getFechaInicio()));
        if (tvDesc != null) tvDesc.setText(activity.getDescripcion());

        if (tvPlazas != null) {
            String plazasInfo = activity.getInscritosConfirmados() + "/" + activity.getCupoMaximo();
            tvPlazas.setText(plazasInfo);
        }

        if (tvCategory != null) {
            tvCategory.setText(activity.getDuracionHoras() + "h");
        }

        if (imgHeader != null) {
            imgHeader.setImageResource(R.drawable.activities1);
        }
    }

    private void setupMode() {
        if (btnJoin == null) return;

        if (isOrgView) {
            // MODO ORGANIZACIÓN
            btnJoin.setText("Ver Inscritos");
            btnJoin.setIconResource(R.drawable.ic_group); // Asegúrate de tener un icono o quítalo
            btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800"))); // Naranja para gestión
            btnJoin.setEnabled(true);
        } else {
            // MODO VOLUNTARIO (Lógica original)
            if (!currentActivity.hayPlazasDisponibles()) {
                btnJoin.setEnabled(false);
                btnJoin.setText("Completo");
                btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#98A2B3")));
            } else {
                btnJoin.setEnabled(true);
                btnJoin.setText("¡Apuntarme!");
                btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4E6AF3")));
            }
        }
    }

    private String formatFecha(String fechaCompleta) {
        if (fechaCompleta == null || fechaCompleta.isEmpty()) return "";
        try {
            return fechaCompleta.substring(0, 10);
        } catch (Exception e) {
            return fechaCompleta;
        }
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnJoin != null) {
            btnJoin.setOnClickListener(v -> {
                if (isOrgView) {
                    mostrarPopupInscritos();
                } else {
                    mostrarPopupExito();
                }
            });
        }

        if (btnOrgProfile != null && !isOrgView) {
            btnOrgProfile.setOnClickListener(v -> {
                if (currentActivity != null) {
                    Intent intent = new Intent(DetailActivity.this, DetailOrganization.class);
                    intent.putExtra("ORG_ID", currentActivity.getIdOrganizacion());
                    intent.putExtra("ORG_NAME", currentActivity.getNombreOrganizacion());
                    startActivity(intent);
                }
            });
        }
    }

    private void mostrarPopupInscritos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_volunteers_list, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Vincular vistas del popup
        RecyclerView rvVolunteers = dialogView.findViewById(R.id.rvVolunteersList);
        ProgressBar loading = dialogView.findViewById(R.id.progressBarVolunteers);
        TextView tvEmpty = dialogView.findViewById(R.id.tvNoVolunteers);
        Button btnClose = dialogView.findViewById(R.id.btnCloseDialog);

        rvVolunteers.setLayoutManager(new LinearLayoutManager(this));

        // Cargar datos
        loading.setVisibility(View.VISIBLE);
        ApiClient.getService().getInscritos(currentActivity.getIdActividad()).enqueue(new Callback<List<VoluntarioResponse>>() {
            @Override
            public void onResponse(Call<List<VoluntarioResponse>> call, Response<List<VoluntarioResponse>> response) {
                loading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    VolunteersAdapter adapter = new VolunteersAdapter(response.body());
                    rvVolunteers.setAdapter(adapter);
                    tvEmpty.setVisibility(View.GONE);
                    rvVolunteers.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvVolunteers.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<VoluntarioResponse>> call, Throwable t) {
                loading.setVisibility(View.GONE);
                tvEmpty.setText("Error de conexión");
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void mostrarPopupExito() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnEntendido = dialogView.findViewById(R.id.btnDialogDismiss);
        if (btnEntendido != null) {
            btnEntendido.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });
        }
        dialog.show();
    }
}