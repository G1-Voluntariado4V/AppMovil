package org.cuatrovientos.voluntariado4v;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserExplore extends AppCompatActivity {

    BottomNavigationView bottomNav;
    RecyclerView rvActivities, rvFilters;
    EditText etSearch;

    // Lista Maestra (Todos los datos) y variables de filtro
    ArrayList<ActivityModel> masterList;
    String currentSearchText = "";
    String currentCategory = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_explore);

        initViews();
        masterList = MockDataProvider.getActivities(); // Cargar datos iniciales

        setupNavigation();
        setupFilters(); // Barra horizontal de categorías
        setupSearch();  // Buscador de texto

        updateList();   // Mostrar lista inicial
    }

    private void initViews() {
        rvActivities = findViewById(R.id.rvActivities);
        rvFilters = findViewById(R.id.rvFilters);
        etSearch = findViewById(R.id.etSearch); // Asegúrate que este ID esté en tu XML
    }

    private void setupFilters() {
        List<String> categories = Arrays.asList("Todos", "Social", "Medioambiente", "Educación", "Deporte");
        rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Reutilizamos el FilterAdapter
        FilterAdapter filterAdapter = new FilterAdapter(categories, category -> {
            currentCategory = category;
            updateList();
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
                updateList();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateList() {
        ArrayList<ActivityModel> filteredList = new ArrayList<>();

        for (ActivityModel item : masterList) {
            boolean matchesSearch = item.getTitle().toLowerCase().contains(currentSearchText);
            boolean matchesCategory = currentCategory.equals("Todos") || item.getCategory().equalsIgnoreCase(currentCategory);

            if (matchesSearch && matchesCategory) {
                filteredList.add(item);
            }
        }

        rvActivities.setLayoutManager(new LinearLayoutManager(this));

        // Configurar adaptador con click listener para ir al detalle
        ActivitiesAdapter adapter = new ActivitiesAdapter(filteredList, ActivitiesAdapter.TYPE_BIG_CARD, (item, position) -> {
            Intent intent = new Intent(UserExplore.this, DetailActivity.class);
            intent.putExtra("extra_activity", item);
            startActivity(intent);
        });

        rvActivities.setAdapter(adapter);
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
                return true;
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