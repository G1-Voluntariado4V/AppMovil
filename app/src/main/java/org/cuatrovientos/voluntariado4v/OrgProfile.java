package org.cuatrovientos.voluntariado4v;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class OrgProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_org_profile);

        // 1. Buscamos el botón
        MaterialButton btnContact = findViewById(R.id.btnContact);

        // 2. Definimos qué hacer al hacer clic
        btnContact.setOnClickListener(v -> {
            String emailAddress = "info@amavir.es"; // Pon aquí el email real
            String subject = "Consulta desde la aplicación VoluntariadoApp";

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // Solo apps de correo
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se encontró aplicación de correo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}