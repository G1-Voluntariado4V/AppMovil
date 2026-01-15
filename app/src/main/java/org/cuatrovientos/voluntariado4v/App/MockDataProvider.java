package org.cuatrovientos.voluntariado4v.App;

import org.cuatrovientos.voluntariado4v.Models.ActivityModel;
import org.cuatrovientos.voluntariado4v.Models.OrganizationModel;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockDataProvider {

    private static MockUser loggedUser; // Usuario simulado

    static {
        initUser();
    }

    // ==========================================
    // CLASE Y MÉTODOS DE USUARIO (SOLUCIÓN ERROR)
    // ==========================================

    public static class MockUser {
        public String nombre;
        public String apellidos;
        public String dni;
        public String telefono;
        public String fechaNacimiento;
        public String observaciones;
        public boolean cochePropio;
        public List<String> idiomas;
        public String rol;
        public String email;

        public MockUser(String nombre, String apellidos, String dni, String telefono, String fechaNacimiento, String observaciones, boolean cochePropio, List<String> idiomas, String rol, String email) {
            this.nombre = nombre;
            this.apellidos = apellidos;
            this.dni = dni;
            this.telefono = telefono;
            this.fechaNacimiento = fechaNacimiento;
            this.observaciones = observaciones;
            this.cochePropio = cochePropio;
            this.idiomas = new ArrayList<>(idiomas);
            this.rol = rol;
            this.email = email;
        }
    }

    private static void initUser() {
        loggedUser = new MockUser(
                "Carlos",
                "Rodríguez",
                "12345678A",
                "+34 635147895",
                "12/06/1990",
                "Soy alérgico al queso, tengo asma y aracnofobia.",
                true,
                Arrays.asList("Español", "Inglés"),
                "Voluntario Activo",
                "carlos.rod@email.com"
        );
    }

    public static MockUser getLoggedUser() {
        return loggedUser;
    }

    // ==========================================
    // DATOS DE ORGANIZACIONES
    // ==========================================

    public static ArrayList<OrganizationModel> getTopOrganizations() {
        ArrayList<OrganizationModel> list = new ArrayList<>();
        list.add(new OrganizationModel(
                "Cruz Roja", "Humanitaria",
                "Movimiento humanitario mundial.",
                "contacto@cruzroja.es", "Calle Lezkairu 12", "www.cruzroja.es",
                R.drawable.squarelogo, R.drawable.widelogo, 150, 4.8));

        list.add(new OrganizationModel(
                "Banco de Alimentos", "Social",
                "Recuperación de excedentes alimenticios.",
                "info@bancoalimentos.es", "Polígono Landaben", "www.bancoalimentos.es",
                R.drawable.amavir, R.drawable.widelogo, 80, 4.5));

        list.add(new OrganizationModel(
                "Amavir", "Tercera Edad",
                "Atención de calidad a mayores.",
                "voluntariado@amavir.es", "Mutilva Alta", "www.amavir.es",
                R.drawable.solera, R.drawable.widelogo, 45, 4.2));
        return list;
    }

    public static OrganizationModel getCurrentOrgProfile() {
        return getTopOrganizations().get(0); // Cruz Roja por defecto
    }

    // ==========================================
    // DATOS DE ACTIVIDADES (COMPARTIDO)
    // ==========================================

    public static ArrayList<ActivityModel> getActivities() {
        ArrayList<ActivityModel> list = new ArrayList<>();

        // --- ACTIVAS ---
        list.add(new ActivityModel(1, "Gran Recogida 2024", "Banco de Alimentos", "Pamplona", "20/10/2024",
                "Recogida en supermercados.", R.drawable.activities1, "Social", "ACTIVE"));

        list.add(new ActivityModel(2, "Acompañamiento Mayores", "Amavir", "Mutilva", "22/10/2024",
                "Paseos con residentes.", R.drawable.activities2, "Social", "ACTIVE"));

        list.add(new ActivityModel(3, "Clases de Español", "Cruz Roja", "Burlada", "25/10/2024",
                "Apoyo lingüístico.", R.drawable.news1, "Educación", "ACTIVE"));

        // --- FINALIZADAS (HISTORIAL) ---
        list.add(new ActivityModel(4, "Limpieza Río Arga", "Cruz Roja", "Pamplona", "15/09/2024",
                "Jornada de limpieza.", R.drawable.news2, "Medioambiente", "FINISHED"));

        list.add(new ActivityModel(5, "Reparto de Juguetes", "Cruz Roja", "Berriozar", "05/01/2024",
                "Campaña de reyes.", R.drawable.news3, "Social", "FINISHED"));

        // --- CANCELADAS ---
        list.add(new ActivityModel(6, "Taller de Cocina", "Cruz Roja", "Sarriguren", "10/10/2024",
                "Cancelado por aforo.", R.drawable.news4, "Social", "CANCELLED"));

        return list;
    }

    // ==========================================
    // MÉTODOS PARA VOLUNTARIO (UserActivities)
    // ==========================================

    public static ArrayList<ActivityModel> getMyActivities() {
        ArrayList<ActivityModel> all = getActivities();
        ArrayList<ActivityModel> myActivities = new ArrayList<>();
        // Simulamos inscripción en 1 y 2
        for (ActivityModel act : all) {
            if (act.getId() == 1 || act.getId() == 2) myActivities.add(act);
        }
        return myActivities;
    }

    public static ArrayList<ActivityModel> getHistoryActivities() {
        ArrayList<ActivityModel> all = getActivities();
        ArrayList<ActivityModel> history = new ArrayList<>();
        // Simulamos historial con las finalizadas
        for (ActivityModel act : all) {
            if ("FINISHED".equals(act.getStatus())) history.add(act);
        }
        return history;
    }

    // ==========================================
    // MÉTODOS PARA ORGANIZACIÓN (OrgDashboard)
    // ==========================================

    public static ArrayList<ActivityModel> getOrgActivitiesByStatus(String statusFilter) {
        ArrayList<ActivityModel> all = getActivities();
        String currentOrgName = getCurrentOrgProfile().getName();
        ArrayList<ActivityModel> filtered = new ArrayList<>();

        for (ActivityModel act : all) {
            if (act.getOrganization().equalsIgnoreCase(currentOrgName)) {
                if (statusFilter == null || act.getStatus().equalsIgnoreCase(statusFilter)) {
                    filtered.add(act);
                }
            }
        }
        return filtered;
    }

    public static int getActiveActivitiesCount() {
        return getOrgActivitiesByStatus("ACTIVE").size();
    }
}