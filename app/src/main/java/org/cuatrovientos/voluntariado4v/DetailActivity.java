package org.cuatrovientos.voluntariado4v;

import android.app.AlertDialog;
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

        // Recibimos el objeto enviado desde UserExplore
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
        // IDs que ya tenías
        tvTitle = findViewById(R.id.tvMainTitle);
        imgHeader = findViewById(R.id.imgLogoHeader);
        btnBack = findViewById(R.id.btnBack);
        btnJoin = findViewById(R.id.btnJoin);
        tvPlazas = findViewById(R.id.tvPlazasCount);

        // El ID de la etiqueta (social, medioambiente...)
        tvCategory = findViewById(R.id.tvTagSocial);

        // IDs nuevos que añadiste al XML
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

        // CATEGORÍA (Texto y Color)
        if (tvCategory != null) {
            tvCategory.setText(activity.getCategory());
            updateCategoryColor(activity.getCategory());
        }

        if (imgHeader != null && activity.getImageResource() != 0) {
            imgHeader.setImageResource(activity.getImageResource());
        }
    }

    // Cambia el color del fondo de la etiqueta según el texto
    private void updateCategoryColor(String category) {
        if (category == null) return;

        // Asegúrate de tener estos drawables creados (o usa el azul por defecto)
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
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
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