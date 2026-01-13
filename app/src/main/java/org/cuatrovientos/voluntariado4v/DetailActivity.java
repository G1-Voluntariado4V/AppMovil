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

    // Componentes de la Interfaz (UI)
    private TextView tvTitle, tvOrg, tvLocation, tvDate, tvDesc, tvPlazas;
    private ImageView imgHeader, btnBack;
    private MaterialButton btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);

        // 1. Inicializar las Vistas
        initViews();

        // 2. Recibir el objeto ActivityModel enviado desde la pantalla anterior
        ActivityModel activity = (ActivityModel) getIntent().getSerializableExtra("extra_activity");

        // 3. Rellenar la interfaz con los datos recibidos
        if (activity != null) {
            populateData(activity);
        } else {
            Toast.makeText(this, "Error al cargar los detalles", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 4. Configurar los botones (Listeners)
        setupListeners();
    }

    /**
     * Vincula las variables de Java con los elementos del XML.
     */
    private void initViews() {
        // IDs que YA existen en tu XML
        tvTitle = findViewById(R.id.tvMainTitle);
        imgHeader = findViewById(R.id.imgLogoHeader);
        btnBack = findViewById(R.id.btnBack);
        btnJoin = findViewById(R.id.btnJoin);
        tvPlazas = findViewById(R.id.tvPlazasCount);

        // IDs que DEBES AÑADIR a tu XML para que sean dinámicos
        tvDate = findViewById(R.id.tvDetailDate);      // Añadir id al TextView de la fecha
        tvLocation = findViewById(R.id.tvDetailLocation); // Añadir id al TextView del lugar
        tvOrg = findViewById(R.id.tvDetailOrg);        // Añadir id al TextView de la organización
        tvDesc = findViewById(R.id.tvDetailDesc);      // Añadir id al TextView de la descripción
    }

    /**
     * Asigna los textos e imágenes del objeto ActivityModel a la pantalla.
     */
    private void populateData(ActivityModel activity) {
        // Usamos comprobaciones (if != null) por si se te olvida poner algún ID en el XML
        if (tvTitle != null) tvTitle.setText(activity.getTitle());
        if (tvOrg != null) tvOrg.setText(activity.getOrganization());
        if (tvLocation != null) tvLocation.setText(activity.getLocation());
        if (tvDate != null) tvDate.setText(activity.getDate());
        if (tvDesc != null) tvDesc.setText(activity.getDescription());

        // Cargar imagen de cabecera
        if (imgHeader != null && activity.getImageResource() != 0) {
            imgHeader.setImageResource(activity.getImageResource());
        }

        // Dato harcoreado/ficticio para las plazas (ya que no está en el modelo todavía)
        if (tvPlazas != null) tvPlazas.setText("5/20");
    }

    /**
     * Configura qué pasa al pulsar los botones.
     */
    private void setupListeners() {
        // Botón Atrás
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // Vuelve a la actividad anterior
                getOnBackPressedDispatcher().onBackPressed();
            });
        }

        // Botón Unirse
        if (btnJoin != null) {
            btnJoin.setOnClickListener(v -> mostrarPopupExito());
        }
    }

    /**
     * Muestra el diálogo de confirmación (Popup)
     */
    private void mostrarPopupExito() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflamos el diseño del popup (dialog_success.xml)
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        // Fondo transparente obligatorio para que se vean las esquinas redondeadas
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Botón "Entendido" dentro del popup
        Button btnEntendido = dialogView.findViewById(R.id.btnDialogDismiss);
        if (btnEntendido != null) {
            btnEntendido.setOnClickListener(v -> {
                dialog.dismiss();
                finish(); // Cierra la actividad y vuelve al listado
            });
        }

        dialog.show();
    }
}