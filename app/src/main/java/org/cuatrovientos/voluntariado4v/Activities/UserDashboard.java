package org.cuatrovientos.voluntariado4v.Activities;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cuatrovientos.voluntariado4v.Fragments.UserActivitiesFragment;
import org.cuatrovientos.voluntariado4v.Fragments.UserExploreFragment;
import org.cuatrovientos.voluntariado4v.Fragments.UserHomeFragment;
import org.cuatrovientos.voluntariado4v.Fragments.UserProfileFragment;
import org.cuatrovientos.voluntariado4v.R;

public class UserDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // Cargar el fragmento por defecto (Home) al iniciar
        if (savedInstanceState == null) {
            loadFragment(new UserHomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new UserHomeFragment();
            } else if (itemId == R.id.nav_explore) {
                selectedFragment = new UserExploreFragment();
            } else if (itemId == R.id.nav_activities) {
                selectedFragment = new UserActivitiesFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new UserProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_user, fragment)
                .commit();
    }
}