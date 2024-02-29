package com.zugusiot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class DeviceOperations {
	private static Queue<String> sensorDataQueue = new LinkedList<>();
	

   // Méthode pour ajouter un objet connecté de type capteur ou actionneur
    public static void addObjetConnecte(Connection connection, Scanner scanner) {
        System.out.println("Ajouter un objet connecté:");
        System.out.print("Nom de l'objet: ");
        String nom = scanner.nextLine(); 
		 scanner.nextLine();
        
        System.out.println("Type (1 pour Capteur, 2 pour Actuateur): ");
        int typeChoice = scanner.nextInt();
        String type = typeChoice == 1 ? "Capteur" : "Actuateur";
        scanner.nextLine(); 

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("État (1 pour actif, 0 pour inactif): ");
        String etat = scanner.nextInt() == 1 ? "actif" : "inactif";
        scanner.nextLine(); 

        System.out.print("Position: ");
        String position = scanner.nextLine();
		
		String code = generateUniqueCode();

        String insertSql = "INSERT INTO objetconnecte (code, nom, type, description, etat, position) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, nom);
            pstmt.setString(3, type);
            pstmt.setString(4, description);
            pstmt.setString(5, etat);
            pstmt.setString(6, position);
            pstmt.executeUpdate();
            System.out.println("Objet connecté ajouté avec succès. Code unique: " + code);
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'objet connecté: " + e.getMessage());
        }
    }
	
	 private static String generateUniqueCode() {
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }



		    public static void simulateSensorData(Connection connection, Scanner scanner) {
        System.out.println("Simuler des données pour un capteur:");
        System.out.print("Entrez le code de l'objet connecté: ");
        String code = scanner.next();

        if (verifySensorCode(connection, code)) {
            Random random = new Random();
            double dataValue = random.nextDouble() * 100;
            sensorDataQueue.add(code + ":" + dataValue);
            System.out.println("Donnée simulée pour le capteur " + code + " ajoutée à la file d'attente.");
        } else {
            System.out.println("Le code spécifié n'existe pas dans la base de données ou n'est pas un capteur.");
        }
        
    }

    
public static void addDataToQueue(String data) {
    sensorDataQueue.add(data);
}


		// Méthode pour vérifier si un code appartient à un capteur


		// Méthode pour traiter les données de la file d'attente et les insérer dans la base de données
				public static void processSensorDataQueue(Connection connection) {
                System.out.println("Début du traitement de la file d'attente des données du capteur.");
        while (!sensorDataQueue.isEmpty()) {
            String data = sensorDataQueue.poll();
            String[] parts = data.split(":");
            String codeSensor = parts[0];
            double value = Double.parseDouble(parts[1]);

            if (verifySensorCode(connection, codeSensor)) {
                String insertSql = "INSERT INTO objetsensor (code_sensor, data) VALUES (?, ?)";
try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
    insertStmt.setString(1, codeSensor);
	
    // Création de chaine Json
    String jsonData = String.format("{\"value\": %.2f}", value);
    insertStmt.setString(2, jsonData);

    int rowsAffected = insertStmt.executeUpdate();
    if (rowsAffected > 0) {
        System.out.println("Données traitées et insérées avec succès pour le capteur " + codeSensor);
    }
} catch (SQLException e) {
    System.err.println("Erreur lors de l'insertion des données du capteur: " + e.getMessage());
}

        }
        System.out.println("Fin du traitement de la file d'attente des données du capteur.");
    }
}

	
	private static boolean verifySensorCode(Connection connection, String codeSensor) {
        String query = "SELECT COUNT(*) FROM objetconnecte WHERE code = ? AND type = 'Capteur'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, codeSensor);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du code capteur: " + e.getMessage());
            
        }
      
        return false;
    }
    





    public static void displayObjetConnecteDetails(Connection connection, Scanner scanner) {
    System.out.println("Entrez le code de l'objet connecté pour voir les détails :");
    String code = scanner.next(); // Lire le code de l'objet connecté

    String sql = "SELECT * FROM objetconnecte WHERE code = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, code);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                // Afficher les détails de base de l'objet connecté
                System.out.println("Détails de l'objet connecté :");
                System.out.println("Code : " + rs.getString("code"));
                System.out.println("Nom : " + rs.getString("nom"));
                System.out.println("Type : " + rs.getString("type"));
                System.out.println("Description : " + rs.getString("description"));
                System.out.println("État : " + rs.getString("etat"));
                System.out.println("Position : " + rs.getString("position"));

                // Afficher les détails supplémentaires en fonction du type
                String type = rs.getString("type");
                if ("Capteur".equals(type)) {
                    // Récupérer et afficher les données spécifiques des capteurs
                    displaySensorData(connection, code);
                } else if ("Actuateur".equals(type)) {
                    // Récupérer et afficher les commandes spécifiques des actionneurs
                    displayActuatorData(connection, code);
                }
            } else {
                System.out.println("Aucun objet connecté trouvé avec le code spécifié.");
            }
        }
    } catch (SQLException e) {
        System.err.println("Erreur lors de la recherche de l'objet connecté: " + e.getMessage());
    }
}

private static void displaySensorData(Connection connection, String code) {
    String sql = "SELECT * FROM objetsensor WHERE code_sensor = ? ORDER BY data_timestamp DESC"; // Ajouté ORDER BY pour trier par timestamp
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, code);
        try (ResultSet rs = pstmt.executeQuery()) {
            boolean found = false;
            while (rs.next()) {  
                if (!found) {
                    System.out.println("Données du capteur :");
                    found = true;
                }
                //  stocke un JSON avec des valeurs de capteur
                String data = rs.getString("data");
                System.out.println("Données : " + data);
                
            }
            if (!found) {
                System.out.println("Aucune donnée trouvée pour le capteur avec le code " + code);
            }
        }
    } catch (SQLException e) {
        System.err.println("Erreur lors de l'affichage des données du capteur: " + e.getMessage());
    }
}


	private static void displayActuatorData(Connection connection, String code) {
		String sql = "SELECT * FROM objetactuator WHERE code_actuator = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, code);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					System.out.println("Détails de l'actionneur :");
					// Supposons que la colonne `actuator_commands` stocke un JSON avec des commandes pour l'actionneur
					String commands = rs.getString("actuator_commands");
					System.out.println("Commandes : " + commands);
					
					String status = rs.getString("actuator_status");
					System.out.println("État actuel : " + status);
					
				} else {
					System.out.println("Aucune commande trouvée pour l'actionneur avec le code " + code);
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur lors de l'affichage des commandes de l'actionneur: " + e.getMessage());
		}
	}




   
}
