package org.cuatrovientos.voluntariado4v;

import java.util.ArrayList;

public class MockDataProvider {

    /**
     * Este metodo simula una llamada a la API que devuelve la lista de actividades.
     */
    public static ArrayList<ActivityModel> getActivities() {
        ArrayList<ActivityModel> dataList = new ArrayList<>();

        dataList.add(new ActivityModel(
                "Gran Recogida",
                "Banco Alimentos",
                "Berriozar",
                "20 Jun",
                "Colabora en la recogida anual de alimentos para las familias más necesitadas de la comarca. Se necesitan voluntarios para clasificación y logística.",
                R.drawable.activities2));

        dataList.add(new ActivityModel(
                "Acompañamiento",
                "Cruz Roja",
                "Pamplona",
                "22 Jun",
                "Programa de acompañamiento a personas mayores que sufren soledad no deseada. Paseos, charlas y actividades lúdicas.",
                R.drawable.activities1));

        dataList.add(new ActivityModel(
                "Limpieza Río Arga",
                "GreenPeace",
                "Rochapea",
                "25 Jun",
                "Jornada de limpieza y concienciación ambiental en las orillas del Río Arga. Guantes y bolsas proporcionados por la organización.",
                R.drawable.carousel1));

        dataList.add(new ActivityModel(
                "Clases de Apoyo",
                "Paris 365",
                "Casco Viejo",
                "30 Jun",
                "Ayuda escolar a niños y niñas de primaria en riesgo de exclusión social. Matemáticas, lengua y lectura.",
                R.drawable.carousel2));

        return dataList;
    }
}