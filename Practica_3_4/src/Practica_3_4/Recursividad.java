package Practica_3_4;

import java.util.ArrayList;
import java.util.regex.Pattern;
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
        Pattern patron = Pattern.compile("[\\s\\t\\n\\p{Punct}]");
        String[] palabras = patron.split(frase);

        StringBuilder resultado = new StringBuilder();
        for (int i = palabras.length - 1; i >= 0; i--) {
            resultado.append(invertirPalabra(palabras[i])).append(" ");
        }
        return resultado.toString().trim(); // Eliminamos el espacio adicional al final
    }

    private static String invertirPalabra(String palabra) {
        if (palabra.isEmpty() || palabra.length() == 1) {
            return palabra;
        } else {
            return invertirPalabra(palabra.substring(1)) + palabra.charAt(0);
        }
    }
    
    //4.3
    public static void posiblesManos(int n, ArrayList<Carta> baraja, ArrayList<Carta> manoActual, int index) {
        if (manoActual.size() == n) {
            System.out.println(manoActual);
            return;
        }

        for (int i = index; i < baraja.size(); i++) {
            manoActual.add(baraja.get(i)); 
            posiblesManos(n, baraja, manoActual, i + 1); 
            manoActual.remove(manoActual.size() - 1); 
        }
    }
    
    //4.4
    public static void filtraManos(int n, ArrayList<Carta> baraja, ArrayList<Carta> manoActual, int index) {
        if (manoActual.size() == n) {
            boolean cumpleCondicion = false;
            for (Carta carta : manoActual) {
                if (carta.esAs()) {
                    cumpleCondicion = true;
                    break;
                }
            }
            
            if (cumpleCondicion) {
                System.out.println(manoActual);
            }
            return;
        }

        for (int i = index; i < baraja.size(); i++) {
            manoActual.add(baraja.get(i));
            filtraManos(n, baraja, manoActual, i + 1);
            manoActual.remove(manoActual.size() - 1);
        }
    }
    
    //4.5
    private static Connection obtenerConexion() throws SQLException {
    	Connection conexion = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/nombre_base_datos?useSSL=false";
            String usuario = "tu_usuario";
            String contraseña = "tu_contraseña";

            conexion = DriverManager.getConnection(url, usuario, contraseña);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error al conectar con la base de datos");
        }

        return conexion;
    }

    public static void guardaManos(String filtro, String mano) {
        Connection conexion = null;
        PreparedStatement statement = null;

        try {
            conexion = obtenerConexion();
            String sql = "INSERT INTO ManoFiltro (filtro, mano) VALUES (?, ?)";
            statement = conexion.prepareStatement(sql);
            statement.setString(1, filtro);
            statement.setString(2, mano);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
        
        int n = 3;
        System.out.println("Posibles manos de " + n + " cartas:");
        posiblesManos(n, baraja, new ArrayList<>(), 0);
        
        //4.4
        System.out.println("Manos que incluyen al menos un As:");
        filtraManos(n, baraja, new ArrayList<>(), 0);
        
        //4.5
        String filtro = "Poker";
        String mano = "[As, As, As, As, 10]";
        guardaManos(filtro, mano);
    
    }
}