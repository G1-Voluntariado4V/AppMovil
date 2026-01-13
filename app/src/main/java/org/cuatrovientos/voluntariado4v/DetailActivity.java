package org.cuatrovientos.voluntariado4v;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);

        // 1. Localizamos el botón "Apuntarme"
        Button btnJoin = findViewById(R.id.btnJoin);

        // 2. Le damos la acción al hacer click
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPopupExito();
            }
        });
    }

    private void mostrarPopupExito() {
        // Crear el Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // "Inflar" (convertir XML a Vista) nuestro diseño personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.item_dialog_success, null);
        builder.setView(dialogView);

        // Crear el diálogo y configurar fondo transparente
        // (CRUCIAL para que se vean las esquinas redondeadas)
        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Configurar el botón "Entendido" dentro del popup
        Button btnEntendido = dialogView.findViewById(R.id.btnDialogDismiss);
        btnEntendido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Cierra el popup

                // Cierra la actividad actual
                finish();
            }
        });

        // Mostrar
        dialog.show();
    }
}