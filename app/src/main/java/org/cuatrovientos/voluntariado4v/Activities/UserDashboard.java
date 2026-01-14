package org.cuatrovientos.voluntariado4v.Activities;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cuatrovientos.voluntariado4v.App.NavigationUtils;
import org.cuatrovientos.voluntariado4v.R;

public class UserDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);

        // Configuración centralizada de navegación
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupNavigation(this, bottomNav, R.id.nav_home);
    }
}