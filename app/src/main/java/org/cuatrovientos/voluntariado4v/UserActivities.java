package org.cuatrovientos.voluntariado4v;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserActivities extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activities);

        // 1. Localizar la barra de navegación
        bottomNav = findViewById(R.id.bottomNavigation);

        // 2. Marcar el 3º icono como seleccionado
        bottomNav.setSelectedItemId(R.id.nav_activities);
    }
}