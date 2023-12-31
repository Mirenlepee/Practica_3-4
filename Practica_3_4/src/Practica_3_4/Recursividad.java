package Practica_3_4;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.sql.*;

public class Recursividad {
    
	//4.1
    public static String invertirFrase(String frase) {
        if (frase.isEmpty() || frase.length() == 1) {
            return frase;
        } else {
            return invertirFrase(frase.substring(1)) + frase.charAt(0);
        }
    }
    
    //4.2
    public static String invertirPalabras(String frase) {
    	StringTokenizer tknr = new StringTokenizer(frase, " \t\n\r\f,.:;!?");
        
    	if (!tknr.hasMoreTokens()) {
            return frase; 
        } else {
        	String palabra = tknr.nextToken();
            int index = frase.indexOf(palabra) + palabra.length();
            String restoFrase = (index < frase.length()) ? frase.substring(index) : "";

            return invertirPalabras(restoFrase) + " " + palabra;
        }
    }
    
    //4.3
    public static ArrayList<Carta> manoActual = new ArrayList<>();
    public static ArrayList<ArrayList<Carta>> manos = new ArrayList<>();
    
    public static ArrayList<ArrayList<Carta>> posiblesManos(int n, ArrayList<Carta> baraja, int index) {
        if (manoActual.size() == n) {
            System.out.println(manoActual);
            return manos;
        }

        for (int i = index; i < baraja.size(); i++) {
        	Carta carta = baraja.get(i);
        	if(!manoActual.contains(carta)) {
        		manoActual.add(carta); 
                posiblesManos(n, baraja, i + 1); 
                manoActual.remove(manoActual.size() - 1); 
        	}     
        }
        return manos;
    }
    
    //4.4
    public static void filtraManos(int n, ArrayList<Carta> baraja, String condicion) {
        if (n <= 0 || baraja.isEmpty()) {
            System.out.println("Esta entrada no es válida.");
            return;
        }
        ArrayList<Carta> manoActual = new ArrayList<>();
        filtrarManosRcr(n, baraja, manoActual, 0, condicion);
    }
    
    private static void filtrarManosRcr(int n, ArrayList<Carta> baraja, ArrayList<Carta> manoActual, int index, String condicion) {
        if (n == 0) {
            if (cumpleCond(manoActual, condicion)) {
                System.out.println(manoActual);
            }
            return;
        }

        for (int i = index; i < baraja.size(); i++) {
            manoActual.add(baraja.get(i));
            filtrarManosRcr(n - 1, baraja, manoActual, i + 1, condicion);
            manoActual.remove(manoActual.size() - 1);
        }
    }

    private static boolean cumpleCond(ArrayList<Carta> mano, String condicion) {
        for (Carta carta : mano) {
            if (carta.getValor().equals(condicion)) {
                return true;
            }
        }
        return false;
    }
    
    //4.5
    private static final String URL = "jdbc:sqlite:baraja.db";

    public static void guardaManos(int n, ArrayList<Carta> baraja, String condicion, String nombreFiltro) {
        if (n <= 0 || baraja.isEmpty()) {
            System.out.println("Entrada no válida.");
            return;
        }

        ArrayList<Carta> manoActual = new ArrayList<>();
        filtrarYGuardarManos(n, baraja, manoActual, 0, condicion, nombreFiltro);
    }
    
    private static void filtrarYGuardarManos(int n, ArrayList<Carta> baraja, ArrayList<Carta> manoActual2, int index, String condicion, String nombreFiltro) {
    	if (n == 0) {
            if (cumpleCond(manoActual, condicion)) {
                guardarManoEnDB(manoActual, condicion, nombreFiltro);
            }
            return;
        }

        for (int i = index; i < baraja.size(); i++) {
            manoActual.add(baraja.get(i));
            filtrarYGuardarManos(n - 1, baraja, manoActual, i + 1, condicion, nombreFiltro);
            manoActual.remove(manoActual.size() - 1);
        }
	}
    
    private static void guardarManoEnDB(ArrayList<Carta> mano, String condicion, String nombreFiltro) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Manos (id INTEGER PRIMARY KEY AUTOINCREMENT, Carta TEXT, Condicion TEXT, Filtro TEXT)";
            try (PreparedStatement createTableStatement = conn.prepareStatement(createTableSQL)) {
                createTableStatement.executeUpdate();
            }

            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Manos (Carta, Condicion, Filtro) VALUES (?, ?, ?)");

            for (Carta carta : mano) {
                pstmt.setString(1, carta.toString());
                pstmt.setString(2, condicion);
                pstmt.setString(3, nombreFiltro);
                pstmt.executeUpdate();
            }

            System.out.println("Mano guardada en la base de datos.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

      
    public static void main(String[] args) {
    	//4.1
        String frase1 = "Hola, mundo!";
        String frase2 = "Recursividad es genial";
        
        System.out.println("Frase original 1: " + frase1);
        System.out.println("Frase invertida 1: " + invertirFrase(frase1));

        System.out.println("Frase original 2: " + frase2);
        System.out.println("Frase invertida 2: " + invertirFrase(frase2));
        
        //4.2
        String frase = "¡Hola, mundo! ¿Cómo estás?";
        
        System.out.println("Frase original: " + frase);
        System.out.println("Frase con palabras invertidas: " + invertirPalabras(frase));
        
        //4.3
        
        ArrayList<Carta> baraja = new ArrayList<>();
        baraja.add(new Carta("Corazones", "As"));
        baraja.add(new Carta("Diamantes", "Reina"));
        baraja.add(new Carta("Picas", "7"));
        baraja.add(new Carta("Tréboles", "10"));
        System.out.println("Baraja creada: " + baraja);
        
        int n = 3;
        System.out.println("Posibles manos de " + n + " cartas:");
        posiblesManos(n, baraja, 0);
        
        //4.4
        System.out.println("Manos que incluyen al menos un As:");
        filtraManos(n, baraja, "As");
        
        //4.5
        guardaManos(2, baraja, "As", "Poker");
        guardaManos(5, baraja, "Reina", "Full");
        guardaManos(5, baraja, "Picas", "Escalera");           
    }
}