package com.zugusiot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Date;

public class DeviceOperations {

    public static void addDevice(Connection connection, Scanner scanner) {
        System.out.println("Ajouter un appareil:");
        System.out.print("CODE: ");
        String id = scanner.next();
        System.out.print("Nom de l'appareil: ");
        String nom = scanner.next();

        String type = "";
        boolean typeValid = false;
        while (!typeValid) {
            System.out.print("Type (1-Capteur, 2-Actuateur) : ");
            String typeInput = scanner.next();
            switch (typeInput) {
                case "1":
                    type = "Capteur";
                    typeValid = true;
                    break;
                case "2":
                    type = "Actuateur";
                    typeValid = true;
                    break;
                default:
                    System.out.println("Ce n'est pas l'entree attendue. Veuillez reessayer.");
            }
        }

        String etat = "";
        boolean etatValid = false;
        while (!etatValid) {
            System.out.print("Etat (0-Inactif, 1-Actif) : ");
            String etatInput = scanner.next();
            switch (etatInput) {
                case "0":
                    etat = "Inactif";
                    etatValid = true;
                    break;
                case "1":
                    etat = "Actif";
                    etatValid = true;
                    break;
                default:
                    System.out.println("Ce n'est pas l'entree attendue. Veuillez reessayer.");
            }
        }

        System.out.print("Position: ");
        String position = scanner.next();

        String dateCreation = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        boolean dateValid = false;
        while (!dateValid) {
            System.out.print("Date de creation (YYYY-MM-DD) : ");
            dateCreation = scanner.next();
            try {
                Date date = dateFormat.parse(dateCreation);
                dateValid = true; // Si la date est valide on sort de la boucle
            } catch (ParseException e) {
                System.out.println("Format de date invalide. Veuillez reessayer avec le format AAAA-MM-JJ.");
            }
        }

        
        System.out.print("Fabricant: ");
        String fabricant = scanner.next();

        String sql = "INSERT INTO devices (id, nom, type, etat, position, creation_date, fabricant) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, nom);
            statement.setString(3, type);
            statement.setString(4, etat);
            statement.setString(5, position);
            statement.setDate(6, java.sql.Date.valueOf(dateCreation)); 
            statement.setString(7, fabricant);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " Success record.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'appareil : " + e.getMessage());
        }
    }

    public static void addDataDevice(Connection connection, Scanner scanner) {
        System.out.println("Add Data for device:");
        System.out.print("ID: ");
        String data_id = scanner.next();
        System.out.print("CODE de l'appareil: ");
        String device_id = scanner.next();
        System.out.print("Valeur: ");
        String data_value = scanner.next();
        System.out.print("Date: ");
        String data_timestamp = scanner.next();

        String sql = "INSERT INTO data_device (data_id, device_id, data_value, data_timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, data_id);
            statement.setString(2, device_id);
            statement.setString(3, data_value);
            statement.setString(4, data_timestamp);

            int rowsAffected = statement.executeUpdate();
            System.out.println(rowsAffected + " Success Data device record.");
        } catch (SQLException e) {
            System.err.println("Erreur add Data device : " + e.getMessage());
        }
    }

    public static void displayDevices(Connection connection) {
        String sql = "SELECT * FROM devices";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String nom = resultSet.getString("nom");
                String type = resultSet.getString("type");
                String etat = resultSet.getString("etat");
                String position = resultSet.getString("position");
                java.sql.Date dateCreation = resultSet.getDate("creation_date");
                java.sql.Date dateMaj = resultSet.getDate("maj_date");
                String fabricant = resultSet.getString("fabricant");

                System.out.println("CODE: " + id + ", Nom: " + nom + ", Type: " + type + ", Etat: " + etat + ", Position: " + position + ", Date de Creation: " + dateCreation + ", Date de Mise a jour: " + dateMaj + ", Fabricant: " + fabricant);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des appareils : " + e.getMessage());
        }
    }

    public static void displayDataDevice(Connection connection) {
        String sql = "SELECT * FROM data_device";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String data_id = resultSet.getString("data_id");
                String device_id = resultSet.getString("device_id");
                String data_value = resultSet.getString("data_value");
                String data_timestamp = resultSet.getString("data_timestamp");

                System.out.println("ID: " + data_id + ", CODE DE L'APPAREIL: " + device_id + ", Valeur: " + data_value + ", DATE ENREGISTREMENT: " + data_timestamp);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des donnees d' appareils : " + e.getMessage());
        }
    }

    public static void updateDevice(Connection connection, Scanner scanner) {
        System.out.println("Mise à jour d'un appareil:");
        System.out.print("Entrez l'ID de l'appareil à mettre à jour: ");
        String id = scanner.next();
        System.out.print("Que voudriez vous mettre a jour ?: Saisissez 1 - Etat, Saisissez 2 - Position, Saisissez autre chose - Pour les deux ");
        String menu = scanner.next();
        if (menu.equals("1")) {
            System.out.print("Nouvel etat de fonctionnement: ");
            String nouvelEtat = scanner.next();

            String sql = "UPDATE devices SET etat = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nouvelEtat);
                statement.setString(2, id);
    
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Appareil mis à jour avec succes.");
                } else {
                    System.out.println("Aucun appareil trouve avec l'ID spécifié.");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la mise à jour de l'appareil : " + e.getMessage());
            }
        }
        else if (menu.equals("2")) {
            System.out.print("Nouvel position: ");
            String nouvelPosition = scanner.next();

            String sql = "UPDATE devices SET position = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nouvelPosition);
                statement.setString(2, id);
    
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Appareil mis à jour avec succes.");
                } else {
                    System.out.println("Aucun appareil trouve avec l'ID specifie.");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la mise a jour de l'appareil : " + e.getMessage());
            }
        }
        else {
            System.out.print("Nouvel état de fonctionnement: ");
            String nouvelEtat = scanner.next();
            System.out.print("Nouvel position: ");
            String nouvelPosition = scanner.next();

            String sql = "UPDATE devices SET etat = ?, position = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nouvelEtat);
                statement.setString(2, nouvelPosition);
                statement.setString(3, id);
    
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Appareil mis a jour avec succes.");
                } else {
                    System.out.println("Aucun appareil trouve avec l'ID specifie.");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la mise a jour de l'appareil : " + e.getMessage());
            }
            
        }

        }

        public static void updateDataDevice(Connection connection, Scanner scanner) {
            System.out.println("Mise a jour de la donnee d'un appareil:");
            System.out.print("Entrez l'ID de la donnee a mettre a jour: ");
            String id = scanner.next();
            System.out.print("Que voudriez vous mettre a jour ?: Saisissez 1 - Valeur, Saisissez 2 - Date ");
            String menu = scanner.next();
            if (menu.equals("1")) {
                System.out.print("Nouvelle valeur de la donnee: ");
                String nouvelData = scanner.next();
    
                String sql = "UPDATE data_device SET data_value = ? WHERE data_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, nouvelData);
                    statement.setString(2, id);
        
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Valeur de l'Appareil mise a jour avec succes.");
                    } else {
                        System.out.println("Aucune donnée trouvée avec l'ID specifie.");
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la mise a jour de la donnee : " + e.getMessage());
                }
            }
            else if (menu.equals("2")) {
                System.out.print("Nouvelle Date: ");
                String nouvelDate = scanner.next();
    
                String sql = "UPDATE data_device SET data_timestamp = ? WHERE data_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, nouvelDate);
                    statement.setString(2, id);
        
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Date de la donnee mis a jour avec succes.");
                    } else {
                        System.out.println("Aucune donnee trouve avec l'ID specifie.");
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la mise a jour de la donnee : " + e.getMessage());
                }
            }
            
    
            }

    public static void deleteDevice(Connection connection, Scanner scanner) {
        System.out.println("Suppression d'un appareil:");
        System.out.print("Entrez l'ID de l'appareil a supprimer: ");
        String id = scanner.next();

        String sql = "DELETE FROM devices WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Appareil supprime avec succes.");
            } else {
                System.out.println("Aucun appareil trouve avec l'ID specifie.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'appareil : " + e.getMessage());
        }
    }

    public static void deleteDataDevice(Connection connection, Scanner scanner) {
        System.out.println("Suppression de la donnee d'un appareil:");
        System.out.print("Entrez l'ID de la donnee a supprimer: ");
        String id = scanner.next();

        String sql = "DELETE FROM data_device WHERE data_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Donnee supprimée avec succès.");
            } else {
                System.out.println("Aucune donnee trouve avec l'ID specifie.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la donnee de l'appareil : " + e.getMessage());
        }
    }
}
