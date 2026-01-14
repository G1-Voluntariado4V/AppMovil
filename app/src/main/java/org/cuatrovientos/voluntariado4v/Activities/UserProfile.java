package org.cuatrovientos.voluntariado4v.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cuatrovientos.voluntariado4v.App.NavigationUtils;
import org.cuatrovientos.voluntariado4v.R;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Navegación Centralizada
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupNavigation(this, bottomNav, R.id.nav_profile);
    }
}