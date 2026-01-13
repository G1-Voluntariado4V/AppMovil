package org.cuatrovientos.voluntariado4v;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton; // Importante para el botón de Google

public class AuthLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_login);

        // Localizamos el botón por su ID (btnGoogleLogin)
        MaterialButton btnLogin = findViewById(R.id.btnGoogleLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar al Dashboard
                Intent intent = new Intent(AuthLogin.this, UserDashboard.class);
                startActivity(intent);
                finish(); // Cerramos Login para que no se pueda volver atrás con el botón 'atrás'
            }
        });
    }
}