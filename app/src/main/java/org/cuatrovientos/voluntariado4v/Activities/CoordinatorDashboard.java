package org.cuatrovientos.voluntariado4v.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.cuatrovientos.voluntariado4v.Fragments.CoordinatorActivitiesFragment;
import org.cuatrovientos.voluntariado4v.Fragments.CoordinatorHomeFragment;
import org.cuatrovientos.voluntariado4v.Fragments.CoordinatorManagementFragment;
import org.cuatrovientos.voluntariado4v.Fragments.CoordinatorProfileFragment;
import org.cuatrovientos.voluntariado4v.Fragments.CoordinatorUsersFragment;
import org.cuatrovientos.voluntariado4v.R;

public class CoordinatorDashboard extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // Instancias de los fragmentos
    private final Fragment homeFragment = new CoordinatorHomeFragment();
    private final Fragment usersFragment = new CoordinatorUsersFragment();
    private final Fragment activitiesFragment = new CoordinatorActivitiesFragment();
    private final Fragment managementFragment = new CoordinatorManagementFragment();
    private final Fragment profileFragment = new CoordinatorProfileFragment();

    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_dashboard);

        bottomNavigationView = findViewById(R.id.nav_view_coordinator);

        // Añadir fragmentos al gestor, ocultando todos menos el inicial
        fm.beginTransaction().add(R.id.coordinator_fragment_container, profileFragment, "5").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.coordinator_fragment_container, managementFragment, "4").hide(managementFragment).commit();
        fm.beginTransaction().add(R.id.coordinator_fragment_container, activitiesFragment, "3").hide(activitiesFragment).commit();
        fm.beginTransaction().add(R.id.coordinator_fragment_container, usersFragment, "2").hide(usersFragment).commit();
        fm.beginTransaction().add(R.id.coordinator_fragment_container, homeFragment, "1").commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                    activeFragment = homeFragment;
                    return true;
                } else if (itemId == R.id.navigation_users) {
                    fm.beginTransaction().hide(activeFragment).show(usersFragment).commit();
                    activeFragment = usersFragment;
                    return true;
                } else if (itemId == R.id.navigation_activities) {
                    fm.beginTransaction().hide(activeFragment).show(activitiesFragment).commit();
                    activeFragment = activitiesFragment;
                    return true;
                } else if (itemId == R.id.navigation_management) {
                    fm.beginTransaction().hide(activeFragment).show(managementFragment).commit();
                    activeFragment = managementFragment;
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    fm.beginTransaction().hide(activeFragment).show(profileFragment).commit();
                    activeFragment = profileFragment;
                    return true;
                }
                return false;
            }
        });
    }
}