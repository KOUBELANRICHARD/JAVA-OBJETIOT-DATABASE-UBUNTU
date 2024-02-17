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
