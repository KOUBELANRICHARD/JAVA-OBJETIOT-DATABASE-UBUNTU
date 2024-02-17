package com.zugusiot;

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
        } catch (Exception e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
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
