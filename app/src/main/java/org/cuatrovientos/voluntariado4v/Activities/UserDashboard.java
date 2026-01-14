package org.cuatrovientos.voluntariado4v.Activities;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cuatrovientos.voluntariado4v.Adapters.DashboardOrganizationsAdapter;
import org.cuatrovientos.voluntariado4v.App.MockDataProvider;
import org.cuatrovientos.voluntariado4v.App.NavigationUtils;
import org.cuatrovientos.voluntariado4v.R;

public class UserDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);

        // Configurar RecyclerView Top Organizaciones
        RecyclerView rvTopOrgs = findViewById(R.id.rvTopOrganizations);
        rvTopOrgs.setLayoutManager(new LinearLayoutManager(this));

        DashboardOrganizationsAdapter adapter = new DashboardOrganizationsAdapter(MockDataProvider.getTopOrganizations());
        rvTopOrgs.setAdapter(adapter);

        // Configuración centralizada de navegación
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupNavigation(this, bottomNav, R.id.nav_home);
    }
}