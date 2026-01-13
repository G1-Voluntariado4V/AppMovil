package org.cuatrovientos.voluntariado4v;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserActivities extends AppCompatActivity {

    // 1. Declaramos las variables de las vistas
    private BottomNavigationView bottomNav;
    private TextView tabActivas, tabHistorial;
    private RecyclerView rvActivas, rvHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activities);

        // 2. Inicializamos las vistas buscando por ID
        bottomNav = findViewById(R.id.bottomNavigation);
        tabActivas = findViewById(R.id.tabActivas);
        tabHistorial = findViewById(R.id.tabHistorial);
        rvActivas = findViewById(R.id.rvActivas);
        rvHistorial = findViewById(R.id.rvHistorial);

        // Configuración del menú inferior
        bottomNav.setSelectedItemId(R.id.nav_activities);
        // Aquí deberías añadir el listener del bottomNav para navegar entre pantallas si hace falta

        // 3. Configurar los Listeners (Clics) de las pestañas
        tabActivas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarPestana(true); // true = mostrar activas
            }
        });

        tabHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarPestana(false); // false = mostrar historial
            }
        });

        // 4. Estado inicial: Mostrar siempre "Activas" al abrir la pantalla
        cambiarPestana(true);

        // TODO: Aquí deberías inicializar tus Adapters y asignarlos a rvActivas y rvHistorial
        // cargarDatosActivas();
        // cargarDatosHistorial();

        // 1. Localizar el botón de Filtros
        // IMPORTANTE: En tu XML este ID era un LinearLayout llamado "btnFilters"
        // y tenía visibility="gone". Asegúrate de ponerlo "visible" en el XML.
        View btnFilters = findViewById(R.id.btnFilters);
        btnFilters.setVisibility(View.VISIBLE); // Forzamos que se vea

        // 2. Abrir el diálogo al hacer clic
        btnFilters.setOnClickListener(v -> {
            FilterDialog dialog = new FilterDialog();
            dialog.show(getSupportFragmentManager(), "FilterDialog");
        });
    }

    /**
     * Metodo para alternar entre la vista de Activas y Historial
     * @param mostrarActivas true para ver activas, false para ver historial
     */
    private void cambiarPestana(boolean mostrarActivas) {
        if (mostrarActivas) {
            // A. MOSTRAR ACTIVAS

            // 1. Visibilidad de las listas
            rvActivas.setVisibility(View.VISIBLE);
            rvHistorial.setVisibility(View.GONE);

            // 2. Estilo Pestaña Activas (Seleccionada)
            tabActivas.setBackgroundResource(R.drawable.bg_tab_selected);
            tabActivas.setTextColor(Color.parseColor("#3D5AFE")); // Azul

            // 3. Estilo Pestaña Historial (Deseleccionada)
            tabHistorial.setBackground(null); // Sin fondo blanco
            tabHistorial.setTextColor(Color.parseColor("#667085")); // Gris

        } else {
            // B. MOSTRAR HISTORIAL

            // 1. Visibilidad de las listas
            rvActivas.setVisibility(View.GONE);
            rvHistorial.setVisibility(View.VISIBLE);

            // 2. Estilo Pestaña Activas (Deseleccionada)
            tabActivas.setBackground(null);
            tabActivas.setTextColor(Color.parseColor("#667085")); // Gris

            // 3. Estilo Pestaña Historial (Seleccionada)
            tabHistorial.setBackgroundResource(R.drawable.bg_tab_selected);
            tabHistorial.setTextColor(Color.parseColor("#3D5AFE")); // Azul
        }
    }
}