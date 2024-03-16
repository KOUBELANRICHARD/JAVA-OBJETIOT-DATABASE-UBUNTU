package com.zugusiot;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class SensorDataHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Votre logique pour gérer /sensor-data
        String response = "Données des capteurs"; // Exemple de réponse
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
