package com.zugusiot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class DevicesManager {

    public static void main(String[] args) {
        // Obtension de la connexion
        try (Connection connection = DatabaseConnection.getConnection()) {
            setupDatabase(connection);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion a la base de données MySQL : " + e.getMessage());
            return;
        }

        // Connection à la base de données
        try (Connection connection = DatabaseConnection.getConnection()) {
            runMenu(connection);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion a la base de données : " + e.getMessage());
        }
    }
    

    private static void setupDatabase(Connection connection) throws SQLException {
        
        try (Statement statement = connection.createStatement()) {
            // Vérification de la base de données si elle existe sinon on la crée
            ResultSet resultSet = statement.executeQuery("SHOW DATABASES LIKE 'zugusiot'");
            if (!resultSet.next()) {
                statement.executeUpdate("CREATE DATABASE zugusiot");
            }
            resultSet.close();
    
            // Connectez-vous à la base de données spécifique et créez les tables nécessaires
            try (Connection dbConnection = DatabaseConnection.getConnection();
                 Statement dbStatement = dbConnection.createStatement()) {
                String createFirstTableSQL = "CREATE TABLE IF NOT EXISTS devices (" +
                                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                                        "nom VARCHAR(25)," +
                                        "type VARCHAR(25)," +
                                        "etat VARCHAR(25)," +
                                        "position VARCHAR(50)," +
                                        "description TEXT," +
                                        "creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                        "maj_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                                        "fabricant VARCHAR(25))";
                dbStatement.executeUpdate(createFirstTableSQL);

                String createSecondTableSQL = "CREATE TABLE IF NOT EXISTS data_device (" +
                                        "data_id INT AUTO_INCREMENT PRIMARY KEY," +
                                        "device_id INT," +
                                        "data_value DECIMAL(10,2)," +
                                        "data_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                        "FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE )" ;
                dbStatement.executeUpdate(createSecondTableSQL);
            }
            
        }
    }
    
    // Menu d'insertion et d'affichage de données
    private static void runMenu(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        do {
            try {
                System.out.println("\nDevices Manager - Menu:");
                System.out.println("1. Ajouter un appareil");
                System.out.println("2. Afficher un appareil");
                System.out.println("3. Modifier un appareil");
                System.out.println("4. Supprimer un appareil");
                System.out.println("------------------------------");
                System.out.println("\n-- Data Device Manager - Menu:");
                System.out.println("5. --- Renseigner un appareil");
                System.out.println("6. --- Afficher les donnees");
                System.out.println("7. --- Modifier les donnees");
                System.out.println("8. --- Supprimer les donnees");
                System.out.println("9. Terminer");
                System.out.print("Veuillez faire un choix : ");
                choice = scanner.nextInt();

                // Consommez la nouvelle ligne restante
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        DeviceOperations.addDevice(connection, scanner);
                        break;
                    case 2:
                        DeviceOperations.displayDevices(connection);
                        break;
                    case 3:
                        DeviceOperations.updateDevice(connection, scanner);
                        break;
                    case 4:
                        DeviceOperations.deleteDevice(connection, scanner);
                        break;
                    case 5:
                        DeviceOperations.addDataDevice(connection, scanner);
                        break;
                    case 6:
                        DeviceOperations.displayDataDevice(connection);
                        break;
                    case 7:
                        DeviceOperations.updateDataDevice(connection, scanner);
                        break;
                    case 8:
                        DeviceOperations.deleteDataDevice(connection, scanner);
                        break;
                    case 9:
                        System.out.println("Fermer le programme.");
                        break;
                    default:
                        System.out.println("Choix invalide, veuillez reessayer.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Veuillez entrer un nombre.");
                scanner.nextLine(); 
            } 
        } while (choice != 9);
        scanner.close();
    }
}
