package controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;

import model.data_structures.ILista;
import model.data_structures.NullException;
import model.data_structures.PosException;
import model.data_structures.VacioException;
import model.data_structures.YoutubeVideo;
import model.logic.Modelo;
import utils.Ordenamiento;
import view.View;

public class Controller<T> {

    private Modelo modelo;
    private View view;
    private Scanner lector;

    public Controller() {
        view = new View();
        lector = new Scanner(System.in).useDelimiter("\n");
    }

    public void run() {
        boolean fin = false;

        while (!fin) {
            view.printMenu();
            int option = leerEntero("Seleccione una opci�n:");
            fin = procesarOpcion(option);
        }
    }

    private boolean procesarOpcion(int option) {
        switch (option) {
            case 1:
                cargarDatos();
                break;
            case 2:
                ejecutarRequerimiento1();
                break;
            case 3:
                ejecutarRequerimiento2();
                break;
            case 4:
                ejecutarRequerimiento3();
                break;
            case 5:
                ejecutarRequerimiento4();
                break;
            case 6:
                ejecutarRequerimiento5();
                break;
            case 7:
                salirDelPrograma();
                return true;
            default:
                manejarOpcionInvalida();
                break;
        }
        return false;
    }

    private void salirDelPrograma() {
        view.printMessage("--------- \n Hasta pronto !! \n---------");
        lector.close();
    }

    private void manejarOpcionInvalida() {
        view.printMessage("--------- \n Opci�n Inv�lida !! \n---------");
    }


    private void cargarDatos() {
        view.printMessage("--------- \nCargar datos");
        modelo = new Modelo(1);
        try {
            modelo.cargar();
            view.printModelo(modelo);
        } catch (IOException e) {
            view.printMessage("Error al cargar los datos: " + e.getMessage());
        }
    }

    private void ejecutarRequerimiento1() {
        String punto1 = leerCadena("Ingrese el nombre del primer punto de conexi�n:");
        String punto2 = leerCadena("Ingrese el nombre del segundo punto de conexi�n:");
        String resultado = modelo.req1String(punto1, punto2);
        view.printMessage(resultado);
    }

    private void ejecutarRequerimiento2() {
        String resultado = modelo.req2String();
        view.printMessage(resultado);
    }

    private void ejecutarRequerimiento3() {
        String pais1 = leerCadena("Ingrese el nombre del primer pa�s:");
        String pais2 = leerCadena("Ingrese el nombre del segundo pa�s:");
        String resultado = modelo.req3String(pais1, pais2);
        view.printMessage(resultado);
    }

    private void ejecutarRequerimiento4() {
        String resultado = modelo.req4String();
        view.printMessage(resultado);
    }

    private void ejecutarRequerimiento5() {
        String landing = leerCadena("Ingrese el nombre del punto de conexi�n:");
        String resultado = modelo.req5String(landing);
        view.printMessage(resultado);
    }

    private String leerCadena(String mensaje) {
        view.printMessage(mensaje);
        return lector.next();
    }

    private int leerEntero(String mensaje) {
        view.printMessage(mensaje);
        while (!lector.hasNextInt()) {
            view.printMessage("Por favor, ingrese un n�mero v�lido.");
            lector.next();
        }
        return lector.nextInt();
    }
}
