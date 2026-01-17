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
import org.cuatrovientos.voluntariado4v.Models.HistorialApiResponse;
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
    private boolean isOrgView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);

        initViews();

        currentActivity = (ActividadResponse) getIntent().getSerializableExtra("actividad");
        isOrgView = getIntent().getBooleanExtra("IS_ORG_VIEW", false);

        if (currentActivity != null) {
            populateData(currentActivity);
            setupMode();
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
        if (tvTitle != null)
            tvTitle.setText(activity.getTitulo());
        if (tvOrg != null)
            tvOrg.setText(activity.getNombreOrganizacion());
        if (tvLocation != null)
            tvLocation.setText(activity.getUbicacion());
        if (tvDate != null)
            tvDate.setText(formatFecha(activity.getFechaInicio()));
        if (tvDesc != null)
            tvDesc.setText(activity.getDescripcion());

        if (tvPlazas != null) {
            String plazasInfo = activity.getInscritosConfirmados() + "/" + activity.getCupoMaximo();
            tvPlazas.setText(plazasInfo);
        }

        if (tvCategory != null) {
            String categoria = activity.getTipo();
            if (categoria == null || categoria.isEmpty()) {
                categoria = "Actividad";
            }
            tvCategory.setText(categoria);

            if (categoria.toLowerCase().contains("medio") || categoria.toLowerCase().contains("natur")) {
                tvCategory.setBackgroundResource(R.drawable.bg_tag_green);
            } else if (categoria.toLowerCase().contains("educ") || categoria.toLowerCase().contains("form")) {
                tvCategory.setBackgroundResource(R.drawable.bg_tag_orange);
            } else {
                tvCategory.setBackgroundResource(R.drawable.bg_tag_blue);
            }
        }

        if (imgHeader != null) {
            String imageUrl = activity.getImageUrl();
            com.bumptech.glide.Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.activities1)
                    .error(
                            com.bumptech.glide.Glide.with(this)
                                    .load("https://placehold.co/600x400/780000/ffffff.png?text=Error")
                                    .centerCrop())
                    .into(imgHeader);
        }
    }

    private void setupMode() {
        if (btnJoin == null)
            return;

        // PRIORIDAD 1: Estado pasado por Intent (desde historial)
        String status = getIntent().getStringExtra("INSCRIPCION_STATUS");
        if (status != null) {
            updateJoinButtonState(status);
            return;
        }

        // Lógica estándar
        if (isOrgView) {
            btnJoin.setText("Ver Inscritos");
            btnJoin.setIconResource(R.drawable.ic_group);
            btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
            btnJoin.setEnabled(true);
        } else {
            // MODO VOLUNTARIO desde Explorar: Comprobar estado asíncronamente
            checkAsyncEnrollmentStatus();

            // Estado inicial
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

    private void checkAsyncEnrollmentStatus() {
        android.content.SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1)
            return;

        ApiClient.getService().getHistorial(userId, userId).enqueue(new Callback<HistorialApiResponse>() {
            @Override
            public void onResponse(Call<HistorialApiResponse> call, Response<HistorialApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getActividades() != null) {
                    for (HistorialApiResponse.InscripcionItem item : response.body().getActividades()) {
                        if (item.toActividadResponse().getId() == currentActivity.getId()) {
                            updateJoinButtonState(item.getEstadoInscripcion());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HistorialApiResponse> call, Throwable t) {
            }
        });
    }

    private void updateJoinButtonState(String status) {
        if (status == null)
            return;
        btnJoin.setEnabled(false);
        if ("Pendiente".equalsIgnoreCase(status)) {
            btnJoin.setText("Solicitud Enviada");
            btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
        } else if ("Aceptada".equalsIgnoreCase(status)) {
            btnJoin.setText("Ya estás apuntado");
            btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        } else {
            btnJoin.setText(status);
            btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        }
    }

    private String formatFecha(String fechaCompleta) {
        if (fechaCompleta == null || fechaCompleta.isEmpty())
            return "";
        try {
            String cleanDate = fechaCompleta.replace("T", " ");
            if (cleanDate.length() > 19)
                cleanDate = cleanDate.substring(0, 19);

            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    java.util.Locale.getDefault());
            java.util.Date date = inputFormat.parse(cleanDate);

            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("d 'de' MMMM",
                    new java.util.Locale("es", "ES"));
            return outputFormat.format(date);
        } catch (Exception e) {
            try {
                return fechaCompleta.substring(0, 10);
            } catch (Exception ex) {
                return fechaCompleta;
            }
        }
    }

    private void setupListeners() {
        if (btnBack != null)
            btnBack.setOnClickListener(v -> finish());
        if (btnJoin != null)
            btnJoin.setOnClickListener(v -> {
                if (isOrgView)
                    mostrarPopupInscritos();
                else
                    realizarInscripcion();
            });
        if (btnOrgProfile != null && !isOrgView) {
            btnOrgProfile.setOnClickListener(v -> {
                if (currentActivity != null) {
                    Intent intent = new Intent(DetailActivity.this, DetailOrganization.class);
                    intent.putExtra("ORG_ID", currentActivity.getIdOrganizacion());
                    intent.putExtra("ORG_NAME", currentActivity.getNombreOrganizacion());
                    intent.putExtra("ORG_IMG", currentActivity.getImgOrganizacion());
                    startActivity(intent);
                }
            });
        }
    }

    private void realizarInscripcion() {
        android.content.SharedPreferences prefs = getSharedPreferences("VoluntariadoPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        btnJoin.setEnabled(false);
        Toast.makeText(this, "Procesando inscripción...", Toast.LENGTH_SHORT).show();

        ApiClient.getService().inscribirse(userId, userId, currentActivity.getIdActividad())
                .enqueue(new Callback<org.cuatrovientos.voluntariado4v.Models.MensajeResponse>() {
                    @Override
                    public void onResponse(Call<org.cuatrovientos.voluntariado4v.Models.MensajeResponse> call,
                            Response<org.cuatrovientos.voluntariado4v.Models.MensajeResponse> response) {
                        btnJoin.setEnabled(true);
                        if (response.isSuccessful()) {
                            mostrarPopupExito();
                            // Actualizar botón localmente
                            updateJoinButtonState("Pendiente");
                        } else {
                            // Ahora con X-User-Id debería funcionar, pero si falla por duplicado:
                            if (response.code() == 409)
                                updateJoinButtonState("Pendiente");
                            Toast.makeText(DetailActivity.this, "No se pudo inscribir (¿Ya estás apuntado?)",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<org.cuatrovientos.voluntariado4v.Models.MensajeResponse> call,
                            Throwable t) {
                        btnJoin.setEnabled(true);
                        Toast.makeText(DetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarPopupInscritos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_volunteers_list, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView rvVolunteers = dialogView.findViewById(R.id.rvVolunteersList);
        ProgressBar loading = dialogView.findViewById(R.id.progressBarVolunteers);
        TextView tvEmpty = dialogView.findViewById(R.id.tvNoVolunteers);
        Button btnClose = dialogView.findViewById(R.id.btnCloseDialog);
        rvVolunteers.setLayoutManager(new LinearLayoutManager(this));

        loading.setVisibility(View.VISIBLE);
        ApiClient.getService().getInscritos(currentActivity.getIdActividad())
                .enqueue(new Callback<List<VoluntarioResponse>>() {
                    @Override
                    public void onResponse(Call<List<VoluntarioResponse>> call,
                            Response<List<VoluntarioResponse>> response) {
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
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnEntendido = dialogView.findViewById(R.id.btnDialogDismiss);
        if (btnEntendido != null)
            btnEntendido.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });
        dialog.show();
    }
}
