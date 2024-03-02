/*
  # Connect the sensor to the A0(Analog 0) pin on the Arduino board

  # the sensor value description
  # 0  ~300     dry soil
  # 300~700     humid soil
  # 700~950     in water
*/
#include <SPI.h>                       // Bibliothèque pour la communication SPI (Serial Peripheral Interface)
#include <WiFi101.h>                   // Bibliothèque pour la gestion de la connectivité WiFi sur des cartes comme Arduino MKR1000
#include <ArduinoHttpClient.h> 


 float moisissure;
// Paramètres de connexion WiFi
char ssid[] = "Rogers8443";             // SSID du réseau WiFi
char pass[] = "connect8443";             // Mot de passe du réseau WiFi 

// Paramètres de connexion au serveur Java
char serverAddress[] = "10.0.0.6"; // Adresse IP ou domaine du serveur
int port = 8080; 

wiFiClient wifi;
HttpClient client = HttpClient(wifi, serverAddress, port);  

void setup(){

  Serial.begin(57600);
  
  // Connexion WiFi
  WiFi.end();                          // Réinitialisation du module WiFi (au cas où il aurait été précédemment connecté)
  WiFi.begin(ssid, pass);              // Connexion au réseau WiFi avec SSID et mot de passe spécifiés

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);                       // Attente de 1 seconde entre chaque tentative
    Serial.println("Connexion en cours..."); // Indication de la tentative de connexion en cours
  }

  // Affiche l'adresse IP assignée à l'Arduino
  IPAddress ip = WiFi.localIP();      
  Serial.print("Adresse IP: ");
  Serial.println(ip);

}

void loop(){

  Serial.print("Moisture Sensor Value:");
   moisissure = analogRead(A0);
     int sensorValue = analogRead(A0);   
	 Serial.println(sensorValue); 
 
  
  // Création du payload (contenu du message) à envoyer au serveur
  String payload = "{\"code\": \"YQDR\"},\"Moisissure\": " + String(moisissure) ";
  Serial.print("Payload: ");
  Serial.println(payload);              // Affichage du payload dans le moniteur série

  client.post("/sensor-data/", "application/json", payload); // Envoi du payload au serveur via une requête POST

  int statusCode = client.responseStatusCode();     // Récupération du code de statut de la réponse du serveur
  String response = client.responseBody();          // Récupération du corps de la réponse du serveur

  // Affichage du code de statut et de la réponse dans le moniteur série
  Serial.print("Code de statut: ");
  Serial.println(statusCode);
  Serial.print("Réponse: ");
  Serial.println(response);

  client.flush();                       // Nettoie le buffer de réception du client
  client.stop();                        // Ferme la connexion au serveur

  delay(5000); 

}