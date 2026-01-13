package org.cuatrovientos.voluntariado4v;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserProfile extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        bottomNav = findViewById(R.id.bottomNavigation);

        // Marcamos el item como seleccionado
        bottomNav.setSelectedItemId(R.id.nav_profile);

        // Listener para los cambios de menú
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) return true;

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_explore) {
                startActivity(new Intent(getApplicationContext(), UserExplore.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_activities) {
                startActivity(new Intent(getApplicationContext(), UserActivities.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

    }
}