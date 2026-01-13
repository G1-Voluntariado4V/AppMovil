package org.cuatrovientos.voluntariado4v;

import android.app.AlertDialog;
import android.content.res.ColorStateList; // IMPORTANTE: Necesario para cambiar el color del botón
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
import com.google.android.material.button.MaterialButton;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvOrg, tvLocation, tvDate, tvDesc, tvPlazas, tvCategory;
    private ImageView imgHeader, btnBack;
    private MaterialButton btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);

        initViews();

        ActivityModel activity = (ActivityModel) getIntent().getSerializableExtra("extra_activity");

        if (activity != null) {
            populateData(activity);
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
    }

    private void populateData(ActivityModel activity) {
        if (tvTitle != null) tvTitle.setText(activity.getTitle());
        if (tvOrg != null) tvOrg.setText(activity.getOrganization());
        if (tvLocation != null) tvLocation.setText(activity.getLocation());
        if (tvDate != null) tvDate.setText(activity.getDate());
        if (tvDesc != null) tvDesc.setText(activity.getDescription());

        // 1. Mostrar texto de plazas (Ej: 15/20)
        if (tvPlazas != null) {
            String plazasInfo = activity.getOccupiedSeats() + "/" + activity.getTotalSeats();
            tvPlazas.setText(plazasInfo);
        }

        // 2. LÓGICA DEL BOTÓN: Comprobar si está lleno
        if (btnJoin != null) {
            if (activity.getOccupiedSeats() >= activity.getTotalSeats()) {
                // CASO: ESTÁ LLENO
                btnJoin.setEnabled(false); // Desactiva el click
                btnJoin.setText("Completo");
                btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#98A2B3"))); // Color Gris
            } else {
                // CASO: HAY SITIO
                btnJoin.setEnabled(true);
                btnJoin.setText("¡Apuntarme!");
                btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4E6AF3"))); // Color Azul Original
            }
        }

        if (tvCategory != null) {
            tvCategory.setText(activity.getCategory());
            updateCategoryColor(activity.getCategory());
        }

        if (imgHeader != null && activity.getImageResource() != 0) {
            imgHeader.setImageResource(activity.getImageResource());
        }
    }

    private void updateCategoryColor(String category) {
        if (category == null) return;
        switch (category.toLowerCase()) {
            case "medioambiente":
                tvCategory.setBackgroundResource(R.drawable.bg_tag_green);
                break;
            case "educación":
                tvCategory.setBackgroundResource(R.drawable.bg_tag_orange);
                break;
            case "social":
            default:
                tvCategory.setBackgroundResource(R.drawable.bg_tag_blue);
                break;
        }
    }

    private void setupListeners() {
        // Volver atrás
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Apuntarse (Solo funcionará si está habilitado por la lógica de arriba)
        if (btnJoin != null) {
            btnJoin.setOnClickListener(v -> mostrarPopupExito());
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