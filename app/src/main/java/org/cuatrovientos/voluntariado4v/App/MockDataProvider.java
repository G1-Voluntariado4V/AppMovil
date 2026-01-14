package org.cuatrovientos.voluntariado4v.App;

import org.cuatrovientos.voluntariado4v.Models.ActivityModel;
import org.cuatrovientos.voluntariado4v.Models.OrganizationModel;
import org.cuatrovientos.voluntariado4v.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MockDataProvider {

    private static List<OrganizationModel> organizations;
    private static List<ActivityModel> activities;

    // Bloque estático para inicializar los datos una sola vez
    static {
        initOrganizations();
        initActivities();
    }

    private static void initOrganizations() {
        organizations = new ArrayList<>();

        organizations.add(new OrganizationModel(
                "Amavir",
                "RESIDENCIA DE MAYORES",
                "Dedicados a mejorar la calidad de vida de las personas mayores y dependientes. Buscamos dar vida a los años.",
                "info@amavir.es",
                R.drawable.amavir,
                R.drawable.activities1,
                500, 4.8));

        organizations.add(new OrganizationModel(
                "Banco de Alimentos",
                "ORGANIZACIÓN BENÉFICA",
                "Luchamos contra el hambre y el desperdicio de alimentos en nuestra comunidad mediante la recogida y distribución.",
                "contacto@bancoalimentos.org",
                R.drawable.squarelogo, // Placeholder
                R.drawable.activities2,
                350, 4.9));

        organizations.add(new OrganizationModel(
                "Cruz Roja",
                "AYUDA HUMANITARIA",
                "Prevenimos y aliviamos el sufrimiento humano en todas las circunstancias, protegiendo la vida y la salud.",
                "voluntariado@cruzroja.es",
                R.drawable.squarelogo, // Placeholder
                R.drawable.news1,
                500, 4.7));

        organizations.add(new OrganizationModel(
                "GreenPeace",
                "MEDIOAMBIENTE",
                "Utilizamos la acción directa no violenta para atraer la atención pública hacia los problemas globales del medio ambiente.",
                "unete@greenpeace.org",
                R.drawable.news5, // Placeholder
                R.drawable.carousel1,
                2000, 4.5));

        organizations.add(new OrganizationModel(
                "Paris 365",
                "COMEDOR SOLIDARIO",
                "Garantizamos tres comidas al día a personas que no pueden acceder a ellas por razones económicas.",
                "ayuda@paris365.org",
                R.drawable.squarelogo, // Placeholder
                R.drawable.carousel2,
                80, 4.9));

        organizations.add(new OrganizationModel(
                "Solera Asistencial",
                "TERCERA EDAD",
                "Servicios asistenciales para la tercera edad, centrados en la atención integral y centrada en la persona.",
                "rrhh@solera.es",
                R.drawable.solera,
                R.drawable.activities1,
                600, 4.6));
    }

    private static void initActivities() {
        activities = new ArrayList<>();

        // AMAVIR
        activities.add(new ActivityModel(
                "Paseo con Mayores", "Amavir", "Residencia Oblatas", "18 Jun",
                "Acompañamiento en paseos por los jardines de la residencia para mejorar la movilidad y el ánimo.",
                "Social", R.drawable.activities1, 10, 10));

        activities.add(new ActivityModel(
                "Taller de Lectura", "Amavir", "Mutilva", "22 Jun",
                "Lectura compartida de prensa y libros clásicos para estimular cognitivamente a los residentes.",
                "Cultural", R.drawable.news2, 2, 5));

        // BANCO DE ALIMENTOS
        activities.add(new ActivityModel(
                "Gran Recogida", "Banco de Alimentos", "Supermercados Varios", "20 Jun",
                "Colabora en la recogida anual de alimentos informando a los clientes y recogiendo donaciones.",
                "Social", R.drawable.activities2, 20, 20)); // COMPLETO

        // CRUZ ROJA
        activities.add(new ActivityModel(
                "Acompañamiento", "Cruz Roja", "Pamplona Centro", "22 Jun",
                "Programa de acompañamiento a personas mayores que sufren soledad no deseada en sus domicilios.",
                "Social", R.drawable.news3, 2, 5));

        activities.add(new ActivityModel(
                "Reparto de Juguetes", "Cruz Roja", "Rochapea", "15 Dic",
                "Clasificación y entrega de juguetes nuevos para niños de familias vulnerables.",
                "Social", R.drawable.news4, 0, 15));

        // GREENPEACE
        activities.add(new ActivityModel(
                "Limpieza Río Arga", "GreenPeace", "Paseo del Arga", "25 Jun",
                "Jornada de limpieza y concienciación ambiental en las orillas del Río Arga.",
                "Medioambiente", R.drawable.carousel1, 45, 50));

        // PARIS 365
        activities.add(new ActivityModel(
                "Clases de Apoyo", "Paris 365", "Casco Viejo", "30 Jun",
                "Ayuda escolar a niños y niñas de primaria en riesgo de exclusión social.",
                "Educación", R.drawable.carousel2, 0, 10));

        activities.add(new ActivityModel(
                "Servicio de Cenas", "Paris 365", "Calle San Lorenzo", "Diario",
                "Apoyo en el servicio de cenas del comedor solidario (preparación mesas y servicio).",
                "Social", R.drawable.carousel3, 3, 3)); // COMPLETO

        // SOLERA
        activities.add(new ActivityModel(
                "Bingo Solidario", "Solera Asistencial", "Ensanche", "05 Jul",
                "Organización y animación de una tarde de bingo para los usuarios del centro de día.",
                "Ocio", R.drawable.activities1, 1, 4));
    }

    // --- MÉTODOS PÚBLICOS ---

    public static ArrayList<ActivityModel> getActivities() {
        return new ArrayList<>(activities);
    }

    public static OrganizationModel getOrganizationDetails(String orgName) {
        if (orgName == null) return null;
        for (OrganizationModel org : organizations) {
            if (org.getName().equalsIgnoreCase(orgName)) {
                return org;
            }
        }
        return new OrganizationModel(
                orgName, "Organización", "Información no disponible.", "contacto@voluntariado.org",
                R.drawable.squarelogo, R.drawable.activities1, 0, 0.0);
    }

    public static ArrayList<ActivityModel> getActivitiesByOrganization(String orgName) {
        ArrayList<ActivityModel> filtered = new ArrayList<>();
        if (orgName == null) return filtered;
        for (ActivityModel act : activities) {
            if (act.getOrganization().equalsIgnoreCase(orgName)) {
                filtered.add(act);
            }
        }
        return filtered;
    }

    public static ArrayList<ActivityModel> getMyActivities() {
        ArrayList<ActivityModel> myList = new ArrayList<>();
        if (!activities.isEmpty()) myList.add(activities.get(0));
        if (activities.size() > 2) myList.add(activities.get(2));
        if (activities.size() > 5) myList.add(activities.get(5));
        return myList;
    }

    public static ArrayList<ActivityModel> getHistoryActivities() {
        ArrayList<ActivityModel> historyList = new ArrayList<>();
        historyList.add(new ActivityModel(
                "Maratón Solidario", "Cruz Roja", "Pamplona", "10 Ene",
                "Reparto de dorsales y avituallamiento para los corredores.",
                "Deporte", R.drawable.activities1, 100, 100));
        historyList.add(new ActivityModel(
                "Reforestación", "Ayto. Pamplona", "Mendillorri", "05 Feb",
                "Jornada de plantación de árboles autóctonos.",
                "Medioambiente", R.drawable.carousel1, 20, 30));
        return historyList;
    }

    /**
     * Devuelve las 3 organizaciones con mayor número de voluntarios.
     */
    public static List<OrganizationModel> getTopOrganizations() {
        // Creamos una copia para no alterar el orden original si fuera necesario preservarlo
        List<OrganizationModel> sortedList = new ArrayList<>(organizations);

        // Ordenar descendente por voluntarios
        Collections.sort(sortedList, new Comparator<OrganizationModel>() {
            @Override
            public int compare(OrganizationModel o1, OrganizationModel o2) {
                return Integer.compare(o2.getVolunteersCount(), o1.getVolunteersCount());
            }
        });

        // Devolver solo las 3 primeras (o menos si no hay suficientes)
        if (sortedList.size() > 3) {
            return sortedList.subList(0, 3);
        }
        return sortedList;
    }
}