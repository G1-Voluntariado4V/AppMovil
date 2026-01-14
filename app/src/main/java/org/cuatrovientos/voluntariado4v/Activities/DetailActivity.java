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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.R;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvOrg, tvLocation, tvDate, tvDesc, tvPlazas, tvCategory;
    private ImageView imgHeader, btnBack;
    private MaterialButton btnJoin, btnOrgProfile;
    private ActividadResponse currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);

        initViews();

        currentActivity = (ActividadResponse) getIntent().getSerializableExtra("actividad");

        if (currentActivity != null) {
            populateData(currentActivity);
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

        if (btnJoin != null) {
            if (!activity.hayPlazasDisponibles()) {
                btnJoin.setEnabled(false);
                btnJoin.setText("Completo");
                btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#98A2B3")));
            } else {
                btnJoin.setEnabled(true);
                btnJoin.setText("¡Apuntarme!");
                btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4E6AF3")));
            }
        }

        if (tvCategory != null) {
            tvCategory.setText(activity.getDuracionHoras() + "h");
        }

        if (imgHeader != null) {
            Glide.with(this)
                    .load(activity.getImageUrl())
                    .placeholder(R.drawable.squarelogo)
                    .into(imgHeader);
        }
    }

    private String formatFecha(String fechaCompleta) {
        if (fechaCompleta == null || fechaCompleta.isEmpty())
            return "";
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
            btnJoin.setOnClickListener(v -> mostrarPopupExito());
        }

        if (btnOrgProfile != null) {
            btnOrgProfile.setOnClickListener(v -> {
                if (currentActivity != null) {
                    Intent intent = new Intent(DetailActivity.this, DetailOrg.class);
                    intent.putExtra("ORG_NAME", currentActivity.getNombreOrganizacion());
                    startActivity(intent);
                }
            });
        }
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