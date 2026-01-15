package org.cuatrovientos.voluntariado4v.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.cuatrovientos.voluntariado4v.Fragments.OrganizationActivitiesFragment;
import org.cuatrovientos.voluntariado4v.Fragments.OrganizationHomeFragment;
import org.cuatrovientos.voluntariado4v.Fragments.OrganizationProfileFragment;
import org.cuatrovientos.voluntariado4v.R;

public class OrganizationDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationOrg);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_org_home) {
                selectedFragment = new OrganizationHomeFragment();
            } else if (itemId == R.id.nav_org_activities) {
                selectedFragment = new OrganizationActivitiesFragment();
            } else if (itemId == R.id.nav_org_profile) {
                selectedFragment = new OrganizationProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_org, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Carga inicial
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_org, new OrganizationHomeFragment())
                    .commit();
        }
    }
}