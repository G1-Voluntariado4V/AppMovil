package org.cuatrovientos.voluntariado4v;

import java.util.ArrayList;

public class MockDataProvider {

    public static ArrayList<ActivityModel> getActivities() {
        ArrayList<ActivityModel> dataList = new ArrayList<>();

        // --- AÑADIDO: Actividad de Amavir para probar el perfil ---
        dataList.add(new ActivityModel(
                "Paseo con Mayores", "Amavir", "Residencia Amavir", "18 Jun",
                "Acompañamiento en paseos por los jardines de la residencia para mejorar la movilidad y el ánimo de los residentes.",
                "Social", R.drawable.amavir, // Usamos su logo o una foto genérica
                5, 10));
        // ----------------------------------------------------------

        dataList.add(new ActivityModel(
                "Gran Recogida", "Banco Alimentos", "Berriozar", "20 Jun",
                "Colabora en la recogida anual de alimentos para las familias más necesitadas.",
                "Social", R.drawable.activities2,
                20, 20));

        dataList.add(new ActivityModel(
                "Acompañamiento", "Cruz Roja", "Pamplona", "22 Jun",
                "Programa de acompañamiento a personas mayores que sufren soledad no deseada.",
                "Social", R.drawable.activities1,
                2, 5));

        dataList.add(new ActivityModel(
                "Limpieza Río Arga", "GreenPeace", "Rochapea", "25 Jun",
                "Jornada de limpieza y concienciación ambiental en las orillas del Río Arga.",
                "Medioambiente", R.drawable.carousel1,
                45, 50));

        dataList.add(new ActivityModel(
                "Clases de Apoyo", "Paris 365", "Casco Viejo", "30 Jun",
                "Ayuda escolar a niños y niñas de primaria en riesgo de exclusión social.",
                "Educación", R.drawable.carousel2,
                0, 10));

        return dataList;
    }

    public static ArrayList<ActivityModel> getMyActivities() {
        ArrayList<ActivityModel> myList = new ArrayList<>();
        ArrayList<ActivityModel> all = getActivities();
        // Simulamos inscripción en algunas
        if (all.size() > 1) myList.add(all.get(1));
        if (all.size() > 3) myList.add(all.get(3));
        return myList;
    }

    public static ArrayList<ActivityModel> getHistoryActivities() {
        ArrayList<ActivityModel> historyList = new ArrayList<>();

        historyList.add(new ActivityModel(
                "Maratón Solidario", "Ayuda en Acción", "Pamplona", "10 Ene",
                "Reparto de dorsales y avituallamiento para los corredores.",
                "Deporte", R.drawable.activities1,
                100, 100));

        historyList.add(new ActivityModel(
                "Reforestación", "Ayto. Pamplona", "Mendillorri", "05 Feb",
                "Jornada de plantación de árboles autóctonos.",
                "Medioambiente", R.drawable.carousel1,
                20, 30));

        return historyList;
    }

    // --- NUEVOS MÉTODOS PARA PERFIL DE ORGANIZACIÓN ---

    public static OrganizationModel getOrganizationDetails(String orgName) {
        // Simulamos la búsqueda. Si es Amavir devolvemos sus datos.
        if (orgName != null && orgName.equalsIgnoreCase("Amavir")) {
            return new OrganizationModel(
                    "Amavir",
                    "ORGANIZACIÓN HUMANITARIA",
                    "Somos una organización dedicada a mejorar la calidad de vida de las personas mayores y dependientes. Trabajamos con voluntarios para ofrecer compañía, apoyo y alegría a nuestros residentes.",
                    "info@amavir.es",
                    R.drawable.amavir,      // Logo
                    R.drawable.activities1, // Cabecera (usamos una foto genérica existente)
                    120,
                    4.8
            );
        }

        // Retorno genérico para pruebas si no es Amavir
        return new OrganizationModel(
                "Organización",
                "ONG",
                "Descripción genérica de la organización.",
                "contacto@ong.org",
                R.drawable.nouser,
                R.drawable.activities2,
                0,
                0.0
        );
    }

    public static ArrayList<ActivityModel> getActivitiesByOrganization(String orgName) {
        ArrayList<ActivityModel> allActivities = getActivities();
        ArrayList<ActivityModel> filteredList = new ArrayList<>();

        if (orgName == null) return filteredList;

        for (ActivityModel activity : allActivities) {
            if (activity.getOrganization().equalsIgnoreCase(orgName)) {
                filteredList.add(activity);
            }
        }
        return filteredList;
    }
}