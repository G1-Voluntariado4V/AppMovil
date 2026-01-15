package org.cuatrovientos.voluntariado4v.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cuatrovientos.voluntariado4v.Adapters.ActividadesApiAdapter;
import org.cuatrovientos.voluntariado4v.Adapters.FilterAdapter;
import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.R;
import org.cuatrovientos.voluntariado4v.api.ApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserExplore extends AppCompatActivity {

    private static final String TAG = "UserExplore";

    private RecyclerView rvActivities, rvFilters;
    private EditText etSearch;
    private ActividadesApiAdapter adapter;

    private List<ActividadResponse> masterList = new ArrayList<>();
    private String currentSearchText = "";
    private String currentCategory = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_explore);

        initViews();
        setupNavigation();
        setupFilters();
        setupSearch();
        loadActividades();
    }

    private void initViews() {
        rvActivities = findViewById(R.id.rvActivities);
        rvFilters = findViewById(R.id.rvFilters);
        etSearch = findViewById(R.id.etSearch);

        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActividadesApiAdapter(new ArrayList<>(), this::onActividadClick);
        rvActivities.setAdapter(adapter);
    }

    private void loadActividades() {
        ApiClient.getService().getActividades().enqueue(new Callback<List<ActividadResponse>>() {
            @Override
            public void onResponse(Call<List<ActividadResponse>> call, Response<List<ActividadResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    masterList = response.body();
                    applyFilters();
                    Log.d(TAG, "Cargadas " + masterList.size() + " actividades");
                } else {
                    Log.e(TAG, "Error: " + response.code());
                    Toast.makeText(UserExplore.this, "Error al cargar actividades", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ActividadResponse>> call, Throwable t) {
                Log.e(TAG, "Error de conexión", t);
                Toast.makeText(UserExplore.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilters() {
        List<String> categories = Arrays.asList("Todos", "Social", "Medioambiente", "Educación", "Deporte", "General");
        rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        FilterAdapter filterAdapter = new FilterAdapter(categories, category -> {
            currentCategory = category;
            applyFilters();
        });
        rvFilters.setAdapter(filterAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().toLowerCase();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void applyFilters() {
        List<ActividadResponse> filtered = new ArrayList<>();
        for (ActividadResponse item : masterList) {
            boolean matchesSearch = item.getTitulo().toLowerCase().contains(currentSearchText);

            // Usamos contains para coincidir con tipos parciales (ej: "Medioambiente"
            // coincide con "Medioambiente tecnologico digital")
            boolean matchesCategory = currentCategory.equals("Todos") ||
                    (item.getTipo() != null && item.getTipo().toLowerCase().contains(currentCategory.toLowerCase()));

            if (matchesSearch && matchesCategory) {
                filtered.add(item);
            }
        }
        adapter.updateData(filtered);
        Log.d(TAG, "Filtrados: " + filtered.size() + " de " + masterList.size() + " (cat=" + currentCategory + ")");
    }

    private void onActividadClick(ActividadResponse actividad, int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("actividad", actividad);
        startActivity(intent);
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_explore);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_explore)
                return true;
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, UserDashboard.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_activities) {
                startActivity(new Intent(this, UserActivities.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, UserProfile.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}