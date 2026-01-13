package org.cuatrovientos.voluntariado4v;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserDashboard extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);

        // 1. Localizar la barra de navegación
        bottomNav = findViewById(R.id.bottomNavigation);

        // 2. Marcar el 1º icono como seleccionado
        bottomNav.setSelectedItemId(R.id.nav_home);
    }
}