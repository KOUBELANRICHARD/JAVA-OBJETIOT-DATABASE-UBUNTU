package com.zugusiot;

import java.io.*;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

public class HttpSensorServer {

    public static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Ajoute un gestionnaire pour les données des capteurs
        server.createContext("/sensor-data", exchange -> {
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
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = now.format(formatter);
                jsonObj.put("dateTime", currentDateTime);

                DeviceOperations.addDataToQueue(jsonObj.toString());
                String response = "Donnée reçue et horodatée avec succès";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }
        });

        // Ajoute un gestionnaire pour l'API des capteurs
        server.createContext("/api/sensors", new ApiSensorsHandler());

        server.setExecutor(null); // Utilise un exécuteur par défaut
        server.start();
        System.out.println("Server started on port 8080");
    }

    public static void main(String[] args) throws IOException {
        startServer();
    }
}
