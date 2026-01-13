package org.cuatrovientos.voluntariado4v;

import java.util.ArrayList;

public class MockDataProvider {

    /**
     * Simula todas las actividades disponibles (Para Explorar)
     */
    public static ArrayList<ActivityModel> getActivities() {
        ArrayList<ActivityModel> dataList = new ArrayList<>();

        dataList.add(new ActivityModel(
                "Gran Recogida",
                "Banco Alimentos",
                "Berriozar",
                "20 Jun",
                "Colabora en la recogida anual de alimentos para las familias más necesitadas de la comarca. Se necesitan voluntarios para clasificación y logística.",
                "Social", // Categoría
                R.drawable.activities2));

        dataList.add(new ActivityModel(
                "Acompañamiento",
                "Cruz Roja",
                "Pamplona",
                "22 Jun",
                "Programa de acompañamiento a personas mayores que sufren soledad no deseada. Paseos, charlas y actividades lúdicas.",
                "Social",
                R.drawable.activities1));

        dataList.add(new ActivityModel(
                "Limpieza Río Arga",
                "GreenPeace",
                "Rochapea",
                "25 Jun",
                "Jornada de limpieza y concienciación ambiental en las orillas del Río Arga. Guantes y bolsas proporcionados por la organización.",
                "Medioambiente",
                R.drawable.carousel1));

        dataList.add(new ActivityModel(
                "Clases de Apoyo",
                "Paris 365",
                "Casco Viejo",
                "30 Jun",
                "Ayuda escolar a niños y niñas de primaria en riesgo de exclusión social. Matemáticas, lengua y lectura.",
                "Educación",
                R.drawable.carousel2));

        return dataList;
    }

    /**
     * Simula las actividades a las que el usuario ya se ha apuntado (Para Mis Actividades - Activas)
     * En una app real, esto vendría filtrado desde el servidor.
     */
    public static ArrayList<ActivityModel> getMyActivities() {
        ArrayList<ActivityModel> myList = new ArrayList<>();
        ArrayList<ActivityModel> all = getActivities();

        // Simulamos que el usuario está apuntado a la "Gran Recogida" y a la "Limpieza"
        if (all.size() > 0) myList.add(all.get(0));
        if (all.size() > 2) myList.add(all.get(2));

        return myList;
    }

    /**
     * Simula las actividades pasadas (Para Mis Actividades - Historial)
     */
    public static ArrayList<ActivityModel> getHistoryActivities() {
        ArrayList<ActivityModel> historyList = new ArrayList<>();

        historyList.add(new ActivityModel(
                "Maratón Solidario",
                "Ayuda en Acción",
                "Pamplona",
                "10 Ene",
                "Reparto de dorsales y avituallamiento para los corredores.",
                "Deporte",
                R.drawable.activities1)); // Puedes cambiar la imagen si tienes otra específica

        historyList.add(new ActivityModel(
                "Reforestación",
                "Ayto. Pamplona",
                "Mendillorri",
                "05 Feb",
                "Jornada de plantación de árboles autóctonos en zonas degradadas.",
                "Medioambiente",
                R.drawable.carousel1));

        return historyList;
    }
}