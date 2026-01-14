package org.cuatrovientos.voluntariado4v;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class UserExplore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_explore);

        // 1. Navegación Centralizada
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupNavigation(this, bottomNav, R.id.nav_explore);

        // 2. Obtener datos
        ArrayList<ActivityModel> dataList = MockDataProvider.getActivities();

        // 3. Configurar RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ActivitiesAdapter adapter = new ActivitiesAdapter(dataList, ActivitiesAdapter.TYPE_BIG_CARD, (item, position) -> {
            Intent intent = new Intent(UserExplore.this, DetailActivity.class);
            intent.putExtra("extra_activity", item);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}