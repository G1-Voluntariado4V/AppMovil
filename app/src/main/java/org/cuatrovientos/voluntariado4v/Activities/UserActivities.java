package org.cuatrovientos.voluntariado4v.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cuatrovientos.voluntariado4v.Adapters.ActivitiesAdapter;
import org.cuatrovientos.voluntariado4v.Adapters.FilterAdapter;
import org.cuatrovientos.voluntariado4v.App.MockDataProvider;
import org.cuatrovientos.voluntariado4v.Models.ActivityModel;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserActivities extends AppCompatActivity {

    BottomNavigationView bottomNav;
    RecyclerView rvActivas, rvHistorial, rvFilters;
    TextView tabActivas, tabHistorial, tvEmptyState;
    EditText etSearch;

    ArrayList<ActivityModel> masterListActivas;
    ArrayList<ActivityModel> masterListHistorial;

    String currentSearchText = "";
    String currentCategory = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activities);

        initViews();
        loadData();
        setupNavigation();

        setupFilters(); // Configura la lista horizontal
        setupSearch();  // Configura el buscador
        setupTabs();

        updateLists();  // Primera carga de datos
    }

    private void initViews() {
        rvActivas = findViewById(R.id.rvMyActivities);
        rvHistorial = findViewById(R.id.rvHistorial);
        rvFilters = findViewById(R.id.rvFilters);
        tabActivas = findViewById(R.id.tabActivas);
        tabHistorial = findViewById(R.id.tabHistorial);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        etSearch = findViewById(R.id.etSearch);
    }

    private void loadData() {
        masterListActivas = MockDataProvider.getMyActivities();
        masterListHistorial = MockDataProvider.getHistoryActivities();
    }

    private void setupFilters() {
        List<String> categories = Arrays.asList("Todos", "Social", "Medioambiente", "Educación", "Deporte");
        rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Usamos el adaptador externo que acabamos de crear
        FilterAdapter filterAdapter = new FilterAdapter(categories, category -> {
            currentCategory = category;
            updateLists();
        });
        rvFilters.setAdapter(filterAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().toLowerCase();
                updateLists();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateLists() {
        ArrayList<ActivityModel> filteredActivas = filterList(masterListActivas);
        ArrayList<ActivityModel> filteredHistorial = filterList(masterListHistorial);

        // Actualizar Activas
        rvActivas.setLayoutManager(new LinearLayoutManager(this));
        rvActivas.setAdapter(new ActivitiesAdapter(filteredActivas, ActivitiesAdapter.TYPE_BIG_CARD, null));

        // Actualizar Historial
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));
        rvHistorial.setAdapter(new ActivitiesAdapter(filteredHistorial, ActivitiesAdapter.TYPE_BIG_CARD, null));

        // Comprobar vacío
        if (tabActivas.getCurrentTextColor() == Color.parseColor("#3D5AFE")) {
            checkEmptyState(filteredActivas);
        } else {
            checkEmptyState(filteredHistorial);
        }
    }

    private ArrayList<ActivityModel> filterList(ArrayList<ActivityModel> source) {
        ArrayList<ActivityModel> result = new ArrayList<>();
        for (ActivityModel item : source) {
            boolean matchesSearch = item.getTitle().toLowerCase().contains(currentSearchText);
            boolean matchesCategory = currentCategory.equals("Todos") || item.getCategory().equalsIgnoreCase(currentCategory);
            if (matchesSearch && matchesCategory) result.add(item);
        }
        return result;
    }

    private void setupTabs() {
        tabActivas.setOnClickListener(v -> {
            updateTabStyles(true);
            rvActivas.setVisibility(View.VISIBLE);
            rvHistorial.setVisibility(View.GONE);
            checkEmptyState(((ActivitiesAdapter) rvActivas.getAdapter()).getDataList());
        });

        tabHistorial.setOnClickListener(v -> {
            updateTabStyles(false);
            rvHistorial.setVisibility(View.VISIBLE);
            rvActivas.setVisibility(View.GONE);
            checkEmptyState(((ActivitiesAdapter) rvHistorial.getAdapter()).getDataList());
        });
    }

    private void updateTabStyles(boolean isActivas) {
        if (isActivas) {
            tabActivas.setBackgroundResource(R.drawable.bg_tab_selected);
            tabActivas.setTextColor(Color.parseColor("#3D5AFE"));
            tabHistorial.setBackground(null);
            tabHistorial.setTextColor(Color.parseColor("#667085"));
        } else {
            tabHistorial.setBackgroundResource(R.drawable.bg_tab_selected);
            tabHistorial.setTextColor(Color.parseColor("#3D5AFE"));
            tabActivas.setBackground(null);
            tabActivas.setTextColor(Color.parseColor("#667085"));
        }
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