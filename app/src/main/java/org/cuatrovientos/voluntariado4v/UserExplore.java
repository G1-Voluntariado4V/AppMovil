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
import java.util.HashSet;
import java.util.Set;

public class UserExplore extends AppCompatActivity {

    private RecyclerView rvActivities, rvFilters;
    private ActivitiesAdapter activitiesAdapter;
    private ArrayList<ActivityModel> allActivities;
    private ArrayList<String> categoryList;

    // Variables de estado para combinar filtros
    private String currentCategory = "Todo";
    private String currentSearchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_explore);

        // 1. Navegación
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupNavigation(this, bottomNav, R.id.nav_explore);

        // 2. Cargar datos
        allActivities = MockDataProvider.getActivities();

        // 3. Configurar RecyclerView de Actividades
        rvActivities = findViewById(R.id.rvActivities);
        rvActivities.setLayoutManager(new LinearLayoutManager(this));

        activitiesAdapter = new ActivitiesAdapter(allActivities, ActivitiesAdapter.TYPE_BIG_CARD, (item, position) -> {
            Intent intent = new Intent(UserExplore.this, DetailActivity.class);
            intent.putExtra("extra_activity", item);
            startActivity(intent);
        });
        rvActivities.setAdapter(activitiesAdapter);

        // 4. Configurar Filtros
        setupFilters();
        setupSearch();
    }

    private void setupFilters() {
        rvFilters = findViewById(R.id.rvFilters);

        // Generar categorías únicas
        categoryList = new ArrayList<>();
        categoryList.add("Todo");

        Set<String> uniqueCategories = new HashSet<>();
        for (ActivityModel activity : allActivities) {
            if (activity.getCategory() != null) {
                uniqueCategories.add(activity.getCategory());
            }
        }
        categoryList.addAll(uniqueCategories);

        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(categoryList, category -> {
            currentCategory = category;
            applyFilters(); // Aplicamos ambos filtros
        });

        rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFilters.setAdapter(categoriesAdapter);
    }

    private void setupSearch() {
        // Buscamos el EditText dentro del layout de búsqueda
        // Nota: Si usas el XML anterior, el EditText es el segundo hijo del LinearLayout 'searchContainer'
        // Lo ideal es darle ID al EditText en el XML, pero esto funcionará con tu código actual
        EditText etSearch = findViewById(R.id.searchContainer).getClass().equals(EditText.class) ?
                (EditText) findViewById(R.id.searchContainer) :
                (EditText) ((android.view.ViewGroup)findViewById(R.id.searchContainer)).getChildAt(1);

        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchText = s.toString().toLowerCase().trim();
                    applyFilters(); // Aplicamos ambos filtros cada vez que se escribe
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    /**
     * Aplica la combinación de Categoría + Búsqueda
     */
    private void applyFilters() {
        ArrayList<ActivityModel> filteredList = new ArrayList<>();

        for (ActivityModel item : allActivities) {
            // 1. Comprobar Categoría
            boolean matchesCategory = currentCategory.equals("Todo") ||
                    item.getCategory().equalsIgnoreCase(currentCategory);

            // 2. Comprobar Texto (Buscamos en título u organización)
            boolean matchesSearch = currentSearchText.isEmpty() ||
                    item.getTitle().toLowerCase().contains(currentSearchText) ||
                    item.getOrganization().toLowerCase().contains(currentSearchText);

            // 3. Si cumple AMBAS, se añade
            if (matchesCategory && matchesSearch) {
                filteredList.add(item);
            }
        }

        activitiesAdapter.updateData(filteredList);
    }
}