package com.zugusiot;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Statement;



public class ApiSensorsHandler implements HttpHandler {
    @Override
public void handle(HttpExchange exchange) throws IOException {
    CorsHeaders.addCorsHeaders(exchange);
    String response = "";
    byte[] responseBytes;
    try {
        //exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        
        if ("GET".equals(exchange.getRequestMethod())) {
            // ... Votre code pour la gestion de GET
            response = getDispositifsList();
            responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);

        } else if ("POST".equals(exchange.getRequestMethod())) {
            // Appel de addNewSensor avec le HttpExchange
            boolean success = addNewSensor(exchange);
        
            // Réponse en fonction du succès de l'opération
            response = success ? "{\"message\":\"Capteur ajouté avec succès\"}" : "{\"error\":\"Erreur lors de l'ajout du capteur\"}";
            responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(success ? 200 : 400, responseBytes.length);

        } else if ("DELETE".equals(exchange.getRequestMethod())) {
            boolean success = deleteSensor(exchange);
            response = success ? "{\"message\":\"Capteur supprimé avec succès\"}" : "{\"error\":\"Erreur lors de la suppression du capteur\"}";
            responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(success ? 200 : 400, responseBytes.length);
        
        
        }   else if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // No content
            return;
        
        
        } else {
            // ... Votre code pour les autres méthodes
            response = "{\"error\":\"Méthode non supportée\"}";
            responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(405, responseBytes.length);
        }
    } catch (Exception e) {
        CorsHeaders.addCorsHeaders(exchange); 
    response = "{\"error\":\"Erreur du serveur\"}";
    responseBytes = response.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
    exchange.sendResponseHeaders(500, responseBytes.length);
        
    }

    try (OutputStream os = exchange.getResponseBody()) {
        os.write(responseBytes);
    }
}




    private String getDispositifsList() {
        JSONArray dispositifsArray = new JSONArray();
        try (Connection connection = DatabaseConnection.getConnection()) {
            Statement stmt = connection.createStatement();
            String query = "SELECT id, code, nom, type, description, etat, position, creation_date, matrice FROM objetconnecte";
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                JSONObject dispositif = new JSONObject();
                dispositif.put("id", rs.getInt("id"));
                dispositif.put("code", rs.getString("code"));
                dispositif.put("nom", rs.getString("nom"));
                dispositif.put("type", rs.getString("type"));
                dispositif.put("description", rs.getString("description"));
                dispositif.put("etat", rs.getString("etat"));
                dispositif.put("position", rs.getString("position"));
                dispositif.put("creation_date", rs.getTimestamp("creation_date").toString());

                String matrice = rs.getString("matrice");
                dispositif.put("matrice", matrice != null ? matrice : "Non spécifié");
                dispositifsArray.put(dispositif);
            }
        } catch (SQLException e) {
            // Gestion des erreurs
            e.printStackTrace();
        }
        return dispositifsArray.toString();
    }

   
    

    private boolean addNewSensor(HttpExchange exchange) {
    // Insérer un nouveau capteur
    try (InputStream is = exchange.getRequestBody();
         Connection connection = DatabaseConnection.getConnection()) {
         
        // Lire le corps de la requête et le convertir en une chaîne JSON
        String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(requestBody);
        
        // Récupérer les valeurs depuis l'objet JSON
        String code = json.optString("code");
        String nom = json.optString("nom");
        String type = json.optString("type");
        String description = json.optString("description");
        String etat = json.optString("etat");
        String position = json.optString("position");
        String matrice = json.optString("matrice");

        // Exemple de requête SQL INSERT (ajustez selon votre schéma de BD)
        String sql = "INSERT INTO objetconnecte (code, nom, type, description, etat, position, matrice) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.setString(2, nom);
            stmt.setString(3, type);
            stmt.setString(4, description);
            stmt.setString(5, etat);
            stmt.setString(6, position);
            stmt.setString(7, matrice);

            // Exécutez la mise à jour et vérifiez le compte de lignes affectées
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

private boolean deleteSensor(HttpExchange exchange) {
    try (InputStream is = exchange.getRequestBody();
         Connection connection = DatabaseConnection.getConnection()) {
         
        // Lire le corps de la requête et le convertir en une chaîne JSON
        String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(requestBody);
        
        // Récupérer l'ID du capteur à supprimer depuis l'objet JSON
        int id = json.optInt("id");

        // Exemple de requête SQL DELETE (ajustez selon votre schéma de BD)
        String sql = "DELETE FROM objetconnecte WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            // Exécutez la mise à jour et vérifiez le compte de lignes affectées
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


}
