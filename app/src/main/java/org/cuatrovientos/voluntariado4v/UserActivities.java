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

    // Declaración de variables para los elementos de la interfaz
    private TextView tabActivas, tabHistorial;
    private RecyclerView rvActivas, rvHistorial;
    private LinearLayout btnFilters;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activities);

        // 1. Inicializar las vistas
        initViews();

        // 2. Configurar la barra de navegación inferior
        setupBottomNavigation();

        // 3. Configurar los clicks de las pestañas
        setupTabs();

        // 4. Configurar los RecyclerViews (Listas)
        // Nota: Aquí deberás asignar tus adaptadores reales más tarde
        setupRecyclerViews();

        // 5. Estado inicial: Mostrar "Activas" por defecto
        showActivas();
    }

    private void initViews() {
        tabActivas = findViewById(R.id.tabActivas);
        tabHistorial = findViewById(R.id.tabHistorial);
        rvActivas = findViewById(R.id.rvActivas);
        rvHistorial = findViewById(R.id.rvHistorial);
        btnFilters = findViewById(R.id.btnFilters);
        bottomNav = findViewById(R.id.bottomNavigation);
    }

    private void setupBottomNavigation() {
        // Marcamos "Mis Actividades" como seleccionado
        // Asegúrate de que R.id.nav_activities coincida con tu menu/bottom_nav_menu.xml
        bottomNav.setSelectedItemId(R.id.nav_activities);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Ir a HomeActivity
                return true;
            } else if (itemId == R.id.nav_explore) {
                // Ir a ExplorarActivity
                return true;
            } else if (itemId == R.id.nav_activities) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Ir a PerfilActivity
                return true;
            }
            return false;
        });
    }

    private void setupTabs() {
        // Listener para la pestaña "Activas"
        tabActivas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivas();
            }
        });

        // Listener para la pestaña "Historial"
        tabHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistorial();
            }
        });
    }

    private void showActivas() {
        // 1. Visibilidad de las Listas
        rvActivas.setVisibility(View.VISIBLE);
        rvHistorial.setVisibility(View.GONE);

        // 2. Visibilidad del Botón Filtros (Oculto en Activas)
        btnFilters.setVisibility(View.GONE);

        // 3. Estilos de los botones (Activas = Seleccionado)
        // Fondo blanco redondeado y texto azul
        tabActivas.setBackgroundResource(R.drawable.bg_tab_selected);
        tabActivas.setTextColor(Color.parseColor("#3D5AFE"));

        // Historial = Deseleccionado (Sin fondo y texto gris)
        tabHistorial.setBackground(null);
        tabHistorial.setTextColor(Color.parseColor("#667085"));
    }

    private void showHistorial() {
        // 1. Visibilidad de las Listas
        rvActivas.setVisibility(View.GONE);
        rvHistorial.setVisibility(View.VISIBLE);

        // 2. Visibilidad del Botón Filtros (Visible en Historial)
        btnFilters.setVisibility(View.VISIBLE);

        // 3. Estilos de los botones (Historial = Seleccionado)
        // Fondo blanco redondeado y texto azul
        tabHistorial.setBackgroundResource(R.drawable.bg_tab_selected);
        tabHistorial.setTextColor(Color.parseColor("#3D5AFE"));

        // Activas = Deseleccionado
        tabActivas.setBackground(null);
        tabActivas.setTextColor(Color.parseColor("#667085"));
    }

    private void setupRecyclerViews() {
        // Configuración básica del LayoutManager
        rvActivas.setLayoutManager(new LinearLayoutManager(this));
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Aquí debes conectar tus Adaptadores cuando los tengas creados.
        // Ejemplo:
        // ActivasAdapter adapterActivas = new ActivasAdapter(listaDeDatos);
        // rvActivas.setAdapter(adapterActivas);

        // HistorialAdapter adapterHistorial = new HistorialAdapter(listaDeHistorial);
        // rvHistorial.setAdapter(adapterHistorial);
    }
}