package org.cuatrovientos.voluntariado4v;

import java.util.ArrayList;

public class MockDataProvider {

    public static ArrayList<ActivityModel> getActivities() {
        ArrayList<ActivityModel> dataList = new ArrayList<>();

        // Añadimos al final: plazas ocupadas, plazas totales
        dataList.add(new ActivityModel(
                "Gran Recogida", "Banco Alimentos", "Berriozar", "20 Jun",
                "Colabora en la recogida anual de alimentos para las familias más necesitadas.",
                "Social", R.drawable.activities2,
                20, 20)); // 20 de 20 ocupadas

        dataList.add(new ActivityModel(
                "Acompañamiento", "Cruz Roja", "Pamplona", "22 Jun",
                "Programa de acompañamiento a personas mayores que sufren soledad no deseada.",
                "Social", R.drawable.activities1,
                2, 5));   // 2 de 5 ocupadas

        dataList.add(new ActivityModel(
                "Limpieza Río Arga", "GreenPeace", "Rochapea", "25 Jun",
                "Jornada de limpieza y concienciación ambiental en las orillas del Río Arga.",
                "Medioambiente", R.drawable.carousel1,
                45, 50)); // 45 de 50 ocupadas

        dataList.add(new ActivityModel(
                "Clases de Apoyo", "Paris 365", "Casco Viejo", "30 Jun",
                "Ayuda escolar a niños y niñas de primaria en riesgo de exclusión social.",
                "Educación", R.drawable.carousel2,
                0, 10));  // 0 de 10 ocupadas

        return dataList;
    }

    public static ArrayList<ActivityModel> getMyActivities() {
        ArrayList<ActivityModel> myList = new ArrayList<>();
        ArrayList<ActivityModel> all = getActivities();
        // Simulamos inscripción en algunas
        if (all.size() > 0) myList.add(all.get(0));
        if (all.size() > 2) myList.add(all.get(2));
        return myList;
    }

    public static ArrayList<ActivityModel> getHistoryActivities() {
        ArrayList<ActivityModel> historyList = new ArrayList<>();

        historyList.add(new ActivityModel(
                "Maratón Solidario", "Ayuda en Acción", "Pamplona", "10 Ene",
                "Reparto de dorsales y avituallamiento para los corredores.",
                "Deporte", R.drawable.activities1,
                100, 100)); // Lleno

        historyList.add(new ActivityModel(
                "Reforestación", "Ayto. Pamplona", "Mendillorri", "05 Feb",
                "Jornada de plantación de árboles autóctonos.",
                "Medioambiente", R.drawable.carousel1,
                20, 30));

        return historyList;
    }
}