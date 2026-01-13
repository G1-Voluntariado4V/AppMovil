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
    ArrayList<ActivityModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_explore);

        // 1. Configurar Barra de Navegación
        setupNavigation();

        // 2. OBTENER DATOS (Desde nuestro proveedor de datos falso/Mock)
        dataList = MockDataProvider.getActivities();

        // 3. Configurar RecyclerView
        recyclerView = findViewById(R.id.rvActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Instanciar adaptador
        ActivitiesAdapter adapter = new ActivitiesAdapter(dataList, ActivitiesAdapter.TYPE_BIG_CARD, new ActivitiesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ActivityModel item, int position) {
                // Al hacer clic, abrimos el detalle y pasamos el objeto
                Intent intent = new Intent(UserExplore.this, DetailActivity.class);
                intent.putExtra("extra_activity", item); // Pasamos el modelo entero
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupNavigation() {
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
    }
}