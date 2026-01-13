package org.cuatrovientos.voluntariado4v;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class UserActivities extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private TextView tabActivas, tabHistorial;
    private RecyclerView rvActivas, rvHistorial;
    private LinearLayout btnFilters;

    // Listas de datos para el adaptador
    private ArrayList<ActivityModel> listaActivas;
    private ArrayList<ActivityModel> listaHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_activities);

        // 1. Inicializar vistas
        bottomNav = findViewById(R.id.bottomNavigation);
        tabActivas = findViewById(R.id.tabActivas);
        tabHistorial = findViewById(R.id.tabHistorial);
        rvActivas = findViewById(R.id.rvActivas);
        rvHistorial = findViewById(R.id.rvHistorial);
        btnFilters = findViewById(R.id.btnFilters); // Asegúrate de que este ID existe en tu XML

        // 2. Configurar Navegación Inferior
        bottomNav.setSelectedItemId(R.id.nav_activities);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_activities) return true;

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_explore) {
                startActivity(new Intent(getApplicationContext(), UserExplore.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        // 3. Configurar Listas y Adaptadores (Reutilizando ActivitiesAdapter)
        setupLists();

        // 4. Configurar Listeners de las Pestañas
        tabActivas.setOnClickListener(v -> cambiarPestana(true));
        tabHistorial.setOnClickListener(v -> cambiarPestana(false));

        // 5. Configurar Botón Filtros
        if (btnFilters != null) {
            btnFilters.setVisibility(View.VISIBLE);
            btnFilters.setOnClickListener(v -> {
                // Abrir diálogo de filtros
                FilterDialog dialog = new FilterDialog();
                dialog.show(getSupportFragmentManager(), "FilterDialog");
            });
        }

        // 6. Estado inicial: Mostrar pestaña "Activas"
        cambiarPestana(true);
    }

    private void setupLists() {
        // --- LISTA 1: ACTIVAS (Tarjetas Grandes) ---
        listaActivas = new ArrayList<>();
        // Datos de prueba (Hardcoded)
        listaActivas.add(new ActivityModel(
                "Recogida Alimentos",
                "Banco Alimentos",
                "Berriozar",
                "20 Jun",
                "Ayuda logística en almacén.",
                R.drawable.activities2));

        listaActivas.add(new ActivityModel(
                "Carrera Solidaria",
                "ANFAS",
                "Antoniutti",
                "24 Jun",
                "Organización y staff del evento.",
                R.drawable.activities1));

        // Configurar RecyclerView Activas
        rvActivas.setLayoutManager(new LinearLayoutManager(this));

        // Instanciamos el adaptador con TYPE_BIG_CARD
        ActivitiesAdapter adapterActivas = new ActivitiesAdapter(listaActivas, ActivitiesAdapter.TYPE_BIG_CARD, new ActivitiesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ActivityModel item, int position) {
                Toast.makeText(UserActivities.this, "Activa seleccionada: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                // Aquí podrías abrir el detalle:
                // startActivity(new Intent(UserActivities.this, DetailActivity.class));
            }
        });
        rvActivas.setAdapter(adapterActivas);


        // --- LISTA 2: HISTORIAL (Tarjetas Pequeñas) ---
        listaHistorial = new ArrayList<>();
        // Datos de prueba
        listaHistorial.add(new ActivityModel(
                "Limpieza Río",
                "GreenPeace",
                "Arga",
                "15 Oct",
                "Limpieza de orillas.",
                R.drawable.carousel1));

        listaHistorial.add(new ActivityModel(
                "Apoyo Escolar",
                "Paris 365",
                "Rochapea",
                "10 Sep",
                "Clases de refuerzo.",
                R.drawable.carousel2));

        // Configurar RecyclerView Historial
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Instanciamos EL MISMO adaptador pero con TYPE_SMALL_CARD
        ActivitiesAdapter adapterHistorial = new ActivitiesAdapter(listaHistorial, ActivitiesAdapter.TYPE_SMALL_CARD, new ActivitiesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ActivityModel item, int position) {
                Toast.makeText(UserActivities.this, "Historial seleccionado: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        rvHistorial.setAdapter(adapterHistorial);
    }

    /**
     * Alterna la visibilidad y estilos entre las pestañas Activas e Historial
     */
    private void cambiarPestana(boolean mostrarActivas) {
        if (mostrarActivas) {
            // Mostrar Activas
            rvActivas.setVisibility(View.VISIBLE);
            rvHistorial.setVisibility(View.GONE);

            // Estilos Pestaña Activas (Seleccionada)
            tabActivas.setBackgroundResource(R.drawable.bg_tab_selected);
            tabActivas.setTextColor(Color.parseColor("#3D5AFE")); // Azul principal

            // Estilos Pestaña Historial (Deseleccionada)
            tabHistorial.setBackground(null);
            tabHistorial.setTextColor(Color.parseColor("#667085")); // Gris
        } else {
            // Mostrar Historial
            rvActivas.setVisibility(View.GONE);
            rvHistorial.setVisibility(View.VISIBLE);

            // Estilos Pestaña Activas (Deseleccionada)
            tabActivas.setBackground(null);
            tabActivas.setTextColor(Color.parseColor("#667085")); // Gris

            // Estilos Pestaña Historial (Seleccionada)
            tabHistorial.setBackgroundResource(R.drawable.bg_tab_selected);
            tabHistorial.setTextColor(Color.parseColor("#3D5AFE")); // Azul principal
        }
    }
}