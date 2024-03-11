package com.zugusiot;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Scanner;

public class DevicesManager {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            setupDatabase(connection);
            runMenu(connection);
            scheduleDataProcessing(); 
            startHttpServer();
        } catch (Exception e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }
    }
    

    private static void startHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/sensor-data", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    if ("POST".equals(exchange.getRequestMethod())) {
                        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        StringBuilder buf = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            buf.append(line);
                        }
                        String body = buf.toString();
                        JSONObject jsonObj = new JSONObject(body);
                        System.out.println("JSON reçu: " + jsonObj.toString()); // Debugging
                        DeviceOperations.addDataToQueue(jsonObj.toString());
                        
                        

                        // Extraire et traiter le code unique et les autres données
                       

                        String response = "Donnée ajoutée à la file d'attente avec succès";
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else {
                        exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
                    }
                }
            });
            server.start();
            System.out.println("Server started on port 8000");
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur : " + e.getMessage());
        }
    }

    private static void setupDatabase(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS kerias");
            statement.executeUpdate("USE kerias");

            String ObjetConnecte = "CREATE TABLE IF NOT EXISTS objetconnecte (" +
                                      "id INT AUTO_INCREMENT PRIMARY KEY," +
                                      "code VARCHAR(4) UNIQUE," +
                                      "nom VARCHAR(255)," +
                                      "type VARCHAR(50)," +
                                      "description TEXT," +
                                      "etat VARCHAR(25)," +
                                      "position VARCHAR(255)," +
                                      "creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                      "maj_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)";

            String ObjetSensor = "CREATE TABLE IF NOT EXISTS objetsensor (" +
                                    "sensor_id INT AUTO_INCREMENT PRIMARY KEY," +
                                    "code_sensor VARCHAR(4) UNIQUE," +
                                    "sensor_name VARCHAR(255)," +
                                    "sensor_description TEXT," +
                                    "data JSON," +
                                    "data_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

            String ObjetActuator = "CREATE TABLE IF NOT EXISTS objetactuator (" +
                                      "actuator_id INT AUTO_INCREMENT PRIMARY KEY," +
                                      "code_actuator VARCHAR(4) UNIQUE," +
                                      "actuator_name VARCHAR(255)," +
                                      "actuator_description TEXT," +
                                      "actuator_commands JSON," +
                                      "actuator_status VARCHAR(255)," +
                                      "data_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

            statement.executeUpdate(ObjetConnecte);
            statement.executeUpdate(ObjetSensor);
            statement.executeUpdate(ObjetActuator);
        }
    }

    private static void runMenu(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\nGestionnaire d'appareils IoT - Menu:");
            System.out.println("1. Ajouter un objet connecté");
            System.out.println("2. Simuler des données de capteur");
            System.out.println("3. Afficher les objets connectés");
            System.out.println("9. Terminer");
            System.out.print("Veuillez faire un choix : ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    // Ajout d'un objet connecté
                    DeviceOperations.addObjetConnecte(connection, scanner);
                    break;
                case 2:
                    // simulation de données
                    DeviceOperations.simulateSensorData(connection, scanner);
                    break;
                case 3:
                    
                    //DeviceOperations.displayObjetConnecte(connection);
                    break;
				 case 4:
					// Affichage des données
					DeviceOperations.displayObjetConnecteDetails(connection, scanner);
					break;
                case 9:
                    System.out.println("Fin du programme.");
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez entre un nombre lié au menu ci-dessus.");
            }
        } while (choice != 9);
        scanner.close();
    }
	
	private static void scheduleDataProcessing() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try (Connection connection = DatabaseConnection.getConnection()) {
                    DeviceOperations.processSensorDataQueue(connection);
                } catch (SQLException e) {
                    System.err.println("Erreur lors du traitement des données : " + e.getMessage());
                }
            }
        }, 0, 5000); // Exécution toutes les 5 secondes
    }
    
    
}
