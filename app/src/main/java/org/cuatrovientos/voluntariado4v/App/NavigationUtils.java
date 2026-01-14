package org.cuatrovientos.voluntariado4v.App;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.cuatrovientos.voluntariado4v.Activities.UserActivities;
import org.cuatrovientos.voluntariado4v.Activities.UserDashboard;
import org.cuatrovientos.voluntariado4v.Activities.UserExplore;
import org.cuatrovientos.voluntariado4v.Activities.UserProfile;
import org.cuatrovientos.voluntariado4v.R;

public class NavigationUtils {

    /**
     * Configura la lógica de navegación para el menú inferior.
     * @param context El contexto de la actividad (this).
     * @param bottomNav La vista del BottomNavigationView.
     * @param selectedItemId El ID del item que debe aparecer marcado.
     */
    public static void setupNavigation(Context context, BottomNavigationView bottomNav, int selectedItemId) {
        // Evitar bucles infinitos seleccionando de nuevo el mismo item
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(selectedItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Si pulsamos el que ya estamos, no hacemos nada
            if (itemId == selectedItemId) return true;

            // Navegación
            if (itemId == R.id.nav_home) {
                startActivity(context, UserDashboard.class);
                return true;
            } else if (itemId == R.id.nav_explore) {
                startActivity(context, UserExplore.class);
                return true;
            } else if (itemId == R.id.nav_activities) {
                startActivity(context, UserActivities.class);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(context, UserProfile.class);
                return true;
            }
            return false;
        });
    }

    private static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        // Flags para limpiar la pila si volvemos al dashboard
        if (cls == UserDashboard.class) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}