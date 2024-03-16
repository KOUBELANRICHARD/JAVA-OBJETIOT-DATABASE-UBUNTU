package com.zugusiot;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
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
            // ... Votre code pour la gestion de POST
            boolean success = addNewSensor();
            response = success ? "{\"message\":\"Capteur ajouté avec succès\"}" : "{\"error\":\"Erreur lors de l'ajout du capteur\"}";
            responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(success ? 200 : 400, responseBytes.length);
        } else {
            // ... Votre code pour les autres méthodes
            response = "{\"error\":\"Méthode non supportée\"}";
            responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(405, responseBytes.length);
        }
    } catch (Exception e) {
        // ... Votre code pour gérer les exceptions
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

   
    

    private boolean addNewSensor() {
        // Simulé l'ajout d'un nouveau capteur. Dans un cas réel, vous traiteriez le corps de la requête ici.
        return true; // Retourne true si l'ajout est réussi
    }
}
