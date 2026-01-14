package org.cuatrovientos.voluntariado4v;

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

    RecyclerView rvActivas, rvHistorial;
    TextView tabActivas, tabHistorial, tvEmptyState;
    ArrayList<ActivityModel> listActivas;
    ArrayList<ActivityModel> listHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activities);

        initViews();

        // Navegación Centralizada
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupNavigation(this, bottomNav, R.id.nav_activities);

        loadData();
        setupRecyclerViews();
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
        // Configuramos ambas listas
        rvActivas.setLayoutManager(new LinearLayoutManager(this));
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Adaptadores (Tipo Big Card, sin listener de click para este ejemplo)
        ActivitiesAdapter adapterActivas = new ActivitiesAdapter(listActivas, ActivitiesAdapter.TYPE_BIG_CARD, null);
        ActivitiesAdapter adapterHistorial = new ActivitiesAdapter(listHistorial, ActivitiesAdapter.TYPE_BIG_CARD, null);

        rvActivas.setAdapter(adapterActivas);
        rvHistorial.setAdapter(adapterHistorial);

        // Estado inicial
        checkEmptyState(listActivas);
    }

    private void setupTabs() {
        tabActivas.setOnClickListener(v -> switchTab(true));
        tabHistorial.setOnClickListener(v -> switchTab(false));
    }

    private void switchTab(boolean showActive) {
        if (showActive) {
            tabActivas.setBackgroundResource(R.drawable.bg_tab_selected);
            tabActivas.setTextColor(Color.parseColor("#3D5AFE"));
            tabHistorial.setBackground(null);
            tabHistorial.setTextColor(Color.parseColor("#667085"));

            rvActivas.setVisibility(View.VISIBLE);
            rvHistorial.setVisibility(View.GONE);
            checkEmptyState(listActivas);
        } else {
            tabHistorial.setBackgroundResource(R.drawable.bg_tab_selected);
            tabHistorial.setTextColor(Color.parseColor("#3D5AFE"));
            tabActivas.setBackground(null);
            tabActivas.setTextColor(Color.parseColor("#667085"));

            rvHistorial.setVisibility(View.VISIBLE);
            rvActivas.setVisibility(View.GONE);
            checkEmptyState(listHistorial);
        }
    }

    private void checkEmptyState(ArrayList<ActivityModel> list) {
        if (list == null || list.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvActivas.setVisibility(View.GONE);
            rvHistorial.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }
}