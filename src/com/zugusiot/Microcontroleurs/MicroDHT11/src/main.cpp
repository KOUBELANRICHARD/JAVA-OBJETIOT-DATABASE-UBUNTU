#include <Arduino.h>

/* Nom de l'auteur :
   Date :
   Description :
   Version :
*/

#include <SPI.h>               // Bibliothèque pour la communication SPI (Serial Peripheral Interface)
#include <WiFi101.h>           // Bibliothèque pour la gestion de la connectivité WiFi sur des cartes comme Arduino MKR1000
#include <ArduinoHttpClient.h> // Bibliothèque pour simplifier la réalisation de requêtes HTTP (GET, POST, etc.)
#include <DHT.h>               // Bibliothèque pour la gestion du capteur DHT (température et humidité)
#include <Adafruit_Sensor.h>
// ... [Le reste du code reste inchangé]

// Paramètres de connexion WiFi
char ssid[] = "Rogers8443";  // SSID du réseau WiFi
char pass[] = "connect8443"; // Mot de passe du réseau WiFi

// Paramètres de connexion au serveur Java
char serverAddress[] = "10.0.0.6"; // Adresse IP ou domaine du serveur
int port = 8000;                   // Port sur lequel le serveur est en écoute

// Paramètres pour le capteur DHT11 (capteur de température et d'humidité)
#define DHTPIN 7          // Pin utilisé pour le DHT11
#define DHTTYPE DHT11     // Type de capteur
DHT dht(DHTPIN, DHTTYPE); // Initialisation du capteur DHT11

WiFiClient wifi;
HttpClient client = HttpClient(wifi, serverAddress, port);

void setup()
{
  Serial.begin(9600); // Initialise la communication série à 9600 bauds
  dht.begin();        // Initialise le capteur DHT11

  // Connexion WiFi
  WiFi.end();             // Réinitialisation du module WiFi (au cas où il aurait été précédemment connecté)
  WiFi.begin(ssid, pass); // Connexion au réseau WiFi avec SSID et mot de passe spécifiés

  while (WiFi.status() != WL_CONNECTED)
  {
    delay(1000);                             // Attente de 1 seconde entre chaque tentative
    Serial.println("Connexion en cours..."); // Indication de la tentative de connexion en cours
  }

  // Affiche l'adresse IP assignée à l'Arduino
  IPAddress ip = WiFi.localIP();
  Serial.print("Adresse IP: ");
  Serial.println(ip);
}

void loop()
{
  float temp = dht.readTemperature(); // Lecture de la température depuis le capteur DHT11
  float humid = dht.readHumidity();   // Lecture de l'humidité

  // Correction de la construction du payload JSON
  String payload = "{\"code\": \"FP78\", \"temperature\": " + String(temp) + ", \"humidite\": " + String(humid) + "}";
  Serial.print("Payload: ");
  Serial.println(payload); // Affichage du payload dans le moniteur série

  client.post("/sensor-data", "application/json", payload); // Assurez-vous que le chemin est correct

  int statusCode = client.responseStatusCode(); // Récupération du code de statut de la réponse du serveur
  String response = client.responseBody();      // Récupération du corps de la réponse du serveur

  // Affichage du code de statut et de la réponse dans le moniteur série
  Serial.print("Code de statut: ");
  Serial.println(statusCode);
  Serial.print("Réponse: ");
  Serial.println(response);

  client.flush(); // Nettoie le buffer de réception du client
  client.stop();  // Ferme la connexion au serveur

  delay(5000); // Attente de 5 secondes avant la prochaine itération
}
