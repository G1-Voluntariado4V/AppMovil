package org.cuatrovientos.voluntariado4v;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserExplore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_explore);

        // 1. Buscar la referencia al menú inferior
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        // 2. Forzar la selección del segundo ítem usando su ID
        bottomNav.setSelectedItemId(R.id.nav_explore);
    }
}