package org.cuatrovientos.voluntariado4v;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class UserExplore extends AppCompatActivity {

    BottomNavigationView bottomNav;
    RecyclerView recyclerView;
    ArrayList<ActivityModel> dataList; // Lista de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_explore);

        // 1. Configurar Barra de Navegación
        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_explore);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_explore) return true;

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                overridePendingTransition(0, 0);
            } else if (itemId == R.id.nav_activities) {
                startActivity(new Intent(getApplicationContext(), UserActivities.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        // 2. Preparar Datos "Hardcoreados"
        dataList = new ArrayList<>();
        // Asegúrate de tener estas imágenes en res/drawable, si no usa R.drawable.ic_launcher_background
        dataList.add(new ActivityModel(
                "Gran Recogida",
                "Banco Alimentos",
                "Berriozar",
                "20 Jun",
                "Colabora en la recogida anual de alimentos.",
                R.drawable.activities2)); // Usa tus imágenes aquí (activities2, carousel1, etc)

        dataList.add(new ActivityModel(
                "Acompañamiento",
                "Cruz Roja",
                "Pamplona",
                "22 Jun",
                "Acompañamiento a personas mayores en soledad.",
                R.drawable.activities1));

        dataList.add(new ActivityModel(
                "Limpieza Río Arga",
                "GreenPeace",
                "Rochapea",
                "25 Jun",
                "Jornada de limpieza y concienciación ambiental.",
                R.drawable.carousel1));

        dataList.add(new ActivityModel(
                "Clases de Apoyo",
                "Paris 365",
                "Casco Viejo",
                "30 Jun",
                "Ayuda escolar a niños y niñas de primaria.",
                R.drawable.carousel2));


        // 3. Configurar RecyclerView
        recyclerView = findViewById(R.id.rvActivities);

        // Layout Manager Vertical
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Instanciar adaptador
        ActivitiesAdapter adapter = new ActivitiesAdapter(dataList, ActivitiesAdapter.TYPE_BIG_CARD, new ActivitiesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ActivityModel item, int position) {
                // Aquí defines qué pasa al hacer click. Por ahora un mensaje.
                Toast.makeText(UserExplore.this, "Seleccionado: " + item.getTitle(), Toast.LENGTH_SHORT).show();

                // Futuro: Abrir DetailActivity pasando datos
                // Intent intent = new Intent(UserExplore.this, DetailActivity.class);
                // startActivity(intent);
            }
        });

        // Asignar adaptador al RecyclerView
        recyclerView.setAdapter(adapter);
    }
}