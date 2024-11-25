package model.logic;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Comparator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import model.data_structures.ArregloDinamico;
import model.data_structures.Country;
import model.data_structures.Edge;
import model.data_structures.GrafoListaAdyacencia;
import model.data_structures.ILista;
import model.data_structures.ITablaSimbolos;
import model.data_structures.Landing;
import model.data_structures.PilaEncadenada;
import model.data_structures.TablaHashSeparteChaining;
import model.data_structures.Vertex;
import utils.Ordenamiento;

public class ModeloHelper {

    public static void cargarPaises(String filePath, GrafoListaAdyacencia grafo, ITablaSimbolos paises) throws IOException {
        Reader in = new FileReader(filePath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);
        for (CSVRecord record : records) {
            if (!record.get(0).isEmpty()) {
                String countryName = record.get(0);
                String capitalName = record.get(1);
                double latitude = Double.parseDouble(record.get(2));
                double longitude = Double.parseDouble(record.get(3));
                String code = record.get(4);
                String continentName = record.get(5);
                float population = Float.parseFloat(record.get(6).replace(".", ""));
                double users = Double.parseDouble(record.get(7).replace(".", ""));

                Country pais = new Country(countryName, capitalName, latitude, longitude, code, continentName, population, users);
                grafo.insertVertex(capitalName, pais);
                paises.put(countryName, pais);
            }
        }
    }

    public static void cargarLandingPoints(String filePath, ITablaSimbolos points) throws IOException {
        Reader in = new FileReader(filePath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);
        for (CSVRecord record : records) {
            String landingId = record.get(0);
            String id = record.get(1);
            String[] location = record.get(2).split(", ");
            String name = location[0];
            String paisnombre = location[location.length - 1];
            double latitude = Double.parseDouble(record.get(3));
            double longitude = Double.parseDouble(record.get(4));

            Landing landing = new Landing(landingId, id, name, paisnombre, latitude, longitude);
            points.put(landingId, landing);
        }
    }

    public static void cargarConexiones(String filePath, GrafoListaAdyacencia grafo, ITablaSimbolos points,
            ITablaSimbolos paises, ITablaSimbolos landingidtabla, ITablaSimbolos nombrecodigo) throws IOException {
        Reader in = new FileReader(filePath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);
        for (CSVRecord record : records) {
            String origin = record.get(0);
            String destination = record.get(1);
            String cableid = record.get(3);
            String[] lengths = record.get(4).split(" ");
            float length = Float.parseFloat(lengths[0]);

            Landing landing1 = (Landing) points.get(origin);
            Landing landing2 = (Landing) points.get(destination);

            grafo.addEdge(landing1.getLandingId(), landing2.getLandingId(), length);
        }
    }

    public static String generarResumen(GrafoListaAdyacencia grafo, ITablaSimbolos paises, ITablaSimbolos points) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("Conexiones: ").append(grafo.edges().size()).append("\n");
        resumen.append("Landing Points: ").append(grafo.vertices().size()).append("\n");
        resumen.append("Países: ").append(paises.size()).append("\n");
        return resumen.toString();
    }

    public static String obtenerClusterInfo(GrafoListaAdyacencia grafo, ITablaSimbolos nombrecodigo,
            ITablaSimbolos landingidtabla, String punto1, String punto2) {
        ITablaSimbolos tabla = grafo.getSSC();
        ILista lista = tabla.valueSet();
        int maxCluster = 0;

        for (int i = 1; i <= lista.size(); i++) {
            try {
                maxCluster = Math.max(maxCluster, (int) lista.getElement(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String fragmento = "La cantidad de componentes conectados es: " + maxCluster;
        try {
            String codigo1 = (String) nombrecodigo.get(punto1);
            String codigo2 = (String) nombrecodigo.get(punto2);

            int cluster1 = (int) tabla.get(codigo1);
            int cluster2 = (int) tabla.get(codigo2);

            fragmento += cluster1 == cluster2 ? "\nLos puntos están en el mismo cluster" : "\nLos puntos no están en el mismo cluster";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragmento;
    }

    public static String obtenerTopLandingPoints(ITablaSimbolos landingidtabla) {
        ILista landingPoints = landingidtabla.valueSet();
        StringBuilder topLandingPoints = new StringBuilder("Top Landing Points:\n");
        int counter = 0;

        for (int i = 1; i <= landingPoints.size(); i++) {
            try {
                ILista conexiones = (ILista) landingPoints.getElement(i);
                if (conexiones.size() > 1 && counter < 10) {
                    Landing landing = (Landing) ((Vertex) conexiones.getElement(1)).getInfo();
                    topLandingPoints.append("\nLanding Point: ").append(landing.getName())
                            .append("\nConnections: ").append(conexiones.size());
                    counter++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return topLandingPoints.toString();
    }

    public static String calcularRutaMinima(GrafoListaAdyacencia grafo, ITablaSimbolos paises, String pais1, String pais2) {
        Country origen = (Country) paises.get(pais1);
        Country destino = (Country) paises.get(pais2);

        if (origen == null || destino == null) {
            return "País no encontrado.";
        }

        PilaEncadenada ruta = grafo.minPath(origen.getCapitalName(), destino.getCapitalName());
        StringBuilder rutaInfo = new StringBuilder("Ruta mínima:\n");
        float totalDistance = 0;

        while (!ruta.isEmpty()) {
            Edge edge = (Edge) ruta.pop();
            float distancia = (float) edge.getWeight();
            totalDistance += distancia;
            rutaInfo.append("De: ").append(edge.getSource().getId())
                    .append(" a: ").append(edge.getDestination().getId())
                    .append(", Distancia: ").append(distancia).append("\n");
        }

        rutaInfo.append("Distancia total: ").append(totalDistance).append(" km.");
        return rutaInfo.toString();
    }

    public static String calcularRedExpansionMinima(GrafoListaAdyacencia grafo, ITablaSimbolos landingidtabla) {
        ILista conexiones = landingidtabla.valueSet();
        StringBuilder info = new StringBuilder("Red de expansión mínima:\n");
        float totalCosto = 0;

        try {
            ILista mst = grafo.mstPrimLazy(conexiones.getElement(1).toString());
            for (int i = 1; i <= mst.size(); i++) {
                Edge edge = (Edge) mst.getElement(i);
                totalCosto += edge.getWeight();
                info.append("De: ").append(edge.getSource().getId())
                        .append(" a: ").append(edge.getDestination().getId())
                        .append(", Costo: ").append(edge.getWeight()).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        info.append("Costo total de la red: ").append(totalCosto);
        return info.toString();
    }

    public static String obtenerPaisesAfectados(String punto, ITablaSimbolos nombrecodigo,
            ITablaSimbolos landingidtabla, ITablaSimbolos paises) {
        String codigo = (String) nombrecodigo.get(punto);
        ILista conexiones = (ILista) landingidtabla.get(codigo);
        ILista paisesAfectados = new ArregloDinamico<>(1);

        for (int i = 1; i <= conexiones.size(); i++) {
            try {
                Vertex vertice = (Vertex) conexiones.getElement(i);
                Landing landing = (Landing) vertice.getInfo();
                Country pais = (Country) paises.get(landing.getPais());
                if (pais != null && !paisesAfectados.contains(pais)) {
                    paisesAfectados.insertElement(pais, paisesAfectados.size() + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        StringBuilder afectados = new StringBuilder("Paises afectados:\n");
        for (int i = 1; i <= paisesAfectados.size(); i++) {
            try {
                Country pais = (Country) paisesAfectados.getElement(i);
                afectados.append("- ").append(pais.getCountryName()).append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return afectados.toString();
    }
}
