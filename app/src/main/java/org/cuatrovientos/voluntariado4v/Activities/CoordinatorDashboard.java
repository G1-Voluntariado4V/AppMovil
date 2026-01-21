package org.cuatrovientos.voluntariado4v.Activities;

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

        // Inicializar fragmentos: añadimos todos y ocultamos los que no son el Home
        fm.beginTransaction().add(R.id.coordinator_fragment_container, profileFragment, "5").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.coordinator_fragment_container, managementFragment, "4").hide(managementFragment).commit();
        fm.beginTransaction().add(R.id.coordinator_fragment_container, activitiesFragment, "3").hide(activitiesFragment).commit();
        fm.beginTransaction().add(R.id.coordinator_fragment_container, usersFragment, "2").hide(usersFragment).commit();
        fm.beginTransaction().add(R.id.coordinator_fragment_container, homeFragment, "1").commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                loadFragment(itemId);
                return true;
            }
        });
    }

    /**
     * Metodo público para cambiar de pestaña programáticamente.
     * Útil para navegar desde el Home a otras secciones al hacer click en tarjetas.
     */
    public void switchToTab(int menuItemId) {
        bottomNavigationView.setSelectedItemId(menuItemId);
    }

    private void loadFragment(int itemId) {
        Fragment targetFragment = null;

        if (itemId == R.id.navigation_home) {
            targetFragment = homeFragment;
        } else if (itemId == R.id.navigation_users) {
            targetFragment = usersFragment;
        } else if (itemId == R.id.navigation_activities) {
            targetFragment = activitiesFragment;
        } else if (itemId == R.id.navigation_management) {
            targetFragment = managementFragment;
        } else if (itemId == R.id.navigation_profile) {
            targetFragment = profileFragment;
        }

        if (targetFragment != null && targetFragment != activeFragment) {
            fm.beginTransaction().hide(activeFragment).show(targetFragment).commit();
            activeFragment = targetFragment;
        }
    }
}