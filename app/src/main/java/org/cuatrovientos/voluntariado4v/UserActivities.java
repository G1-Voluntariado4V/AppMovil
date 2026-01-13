package org.cuatrovientos.voluntariado4v;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class UserActivities extends AppCompatActivity {

    BottomNavigationView bottomNav;
    RecyclerView rvActivas, rvHistorial;
    TextView tabActivas, tabHistorial, tvEmptyState;
    ArrayList<ActivityModel> listActivas;
    ArrayList<ActivityModel> listHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activities);

        initViews();
        setupNavigation();
        loadData();
        setupRecyclerViews(); // <--- Aquí está el cambio
        setupTabs();
    }

    private void initViews() {
        rvActivas = findViewById(R.id.rvMyActivities);
        rvHistorial = findViewById(R.id.rvHistorial);
        tabActivas = findViewById(R.id.tabActivas);
        tabHistorial = findViewById(R.id.tabHistorial);
        tvEmptyState = findViewById(R.id.tvEmptyState);
    }

    private void loadData() {
        listActivas = MockDataProvider.getMyActivities();
        listHistorial = MockDataProvider.getHistoryActivities();
    }

    private void setupRecyclerViews() {
        // --- Lista ACTIVAS ---
        rvActivas.setLayoutManager(new LinearLayoutManager(this));

        // Usamos TYPE_BIG_CARD para ver la etiqueta "Social", etc.
        // Pasamos 'null' como listener para que NO SE PUEDA HACER CLICK
        ActivitiesAdapter adapterActivas = new ActivitiesAdapter(listActivas, ActivitiesAdapter.TYPE_BIG_CARD, null);
        rvActivas.setAdapter(adapterActivas);

        // --- Lista HISTORIAL ---
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Usamos TYPE_BIG_CARD también aquí para mantener la coherencia visual con las etiquetas
        // También pasamos 'null' para bloquear el click
        ActivitiesAdapter adapterHistorial = new ActivitiesAdapter(listHistorial, ActivitiesAdapter.TYPE_BIG_CARD, null);
        rvHistorial.setAdapter(adapterHistorial);

        checkEmptyState(listActivas);
    }

    private void setupTabs() {
        // Pestaña ACTIVAS
        tabActivas.setOnClickListener(v -> {
            // Estilos
            tabActivas.setBackgroundResource(R.drawable.bg_tab_selected);
            tabActivas.setTextColor(Color.parseColor("#3D5AFE"));
            tabHistorial.setBackground(null);
            tabHistorial.setTextColor(Color.parseColor("#667085"));

            // Visibilidad
            rvActivas.setVisibility(View.VISIBLE);
            rvHistorial.setVisibility(View.GONE);

            checkEmptyState(listActivas);
        });

        // Pestaña HISTORIAL
        tabHistorial.setOnClickListener(v -> {
            // Estilos
            tabHistorial.setBackgroundResource(R.drawable.bg_tab_selected);
            tabHistorial.setTextColor(Color.parseColor("#3D5AFE"));
            tabActivas.setBackground(null);
            tabActivas.setTextColor(Color.parseColor("#667085"));

            // Visibilidad
            rvHistorial.setVisibility(View.VISIBLE);
            rvActivas.setVisibility(View.GONE);

            checkEmptyState(listHistorial);
        });
    }

    private void checkEmptyState(ArrayList<ActivityModel> list) {
        if (list == null || list.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvActivas.setVisibility(View.GONE);
            rvHistorial.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            if (tabActivas.getCurrentTextColor() == Color.parseColor("#3D5AFE")) {
                rvActivas.setVisibility(View.VISIBLE);
            } else {
                rvHistorial.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupNavigation() {
        bottomNav = findViewById(R.id.bottomNavigation);
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
    }
}