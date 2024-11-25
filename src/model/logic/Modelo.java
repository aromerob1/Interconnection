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
import model.data_structures.NodoTS;
import model.data_structures.TablaHashLinearProbing;
import model.data_structures.TablaHashSeparteChaining;

public class Modelo {
    private ILista datos;
    private GrafoListaAdyacencia grafo;
    private ITablaSimbolos paises;
    private ITablaSimbolos points;
    private ITablaSimbolos landingidtabla;
    private ITablaSimbolos nombrecodigo;

    public Modelo(int capacidad) {
        datos = new ArregloDinamico<>(capacidad);
    }

    public int darTamano() {
        return datos.size();
    }

    public void cargar() throws IOException {
        grafo = new GrafoListaAdyacencia(2);
        paises = new TablaHashLinearProbing(2);
        points = new TablaHashLinearProbing(2);
        landingidtabla = new TablaHashSeparteChaining(2);
        nombrecodigo = new TablaHashSeparteChaining(2);

        ModeloHelper.cargarPaises("./data/countries.csv", grafo, paises);
        ModeloHelper.cargarLandingPoints("./data/landing_points.csv", points);
        ModeloHelper.cargarConexiones("./data/connections.csv", grafo, points, paises, landingidtabla, nombrecodigo);
    }

    public String toString() {
        return ModeloHelper.generarResumen(grafo, paises, points);
    }

    public String req1String(String punto1, String punto2) {
        return ModeloHelper.obtenerClusterInfo(grafo, nombrecodigo, landingidtabla, punto1, punto2);
    }

    public String req2String() {
        return ModeloHelper.obtenerTopLandingPoints(landingidtabla);
    }

    public String req3String(String pais1, String pais2) {
        return ModeloHelper.calcularRutaMinima(grafo, paises, pais1, pais2);
    }

    public String req4String() {
        return ModeloHelper.calcularRedExpansionMinima(grafo, landingidtabla);
    }

    public String req5String(String punto) {
        return ModeloHelper.obtenerPaisesAfectados(punto, nombrecodigo, landingidtabla, paises);
    }
}
