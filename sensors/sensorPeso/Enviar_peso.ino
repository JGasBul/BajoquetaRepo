#include "HX711.h"
#include <M5Stack.h>
using namespace std;
#include "WiFi.h"
#include "AsyncUDP.h"
#include "PubSubClient.h" // Connect and publish to the MQTT broker



AsyncUDP udp;

 
// Parámetro para calibrar el peso y el sensor
float factor_calibracion = 24550.0; //Factor de calibración 
//20780.0


// Variable para almacenar el estado del PULSADOR
int estado;

// Reemplazar estos valores con los detalles de su servidor MQTT
const char* MQTT_SERVER = "192.168.146.53";
const int MQTT_PORT = 1883;

// Reemplazar este valor con el tópico al que desea suscribirse
const char* MQTT_TOPIC = "home/mov/led";

// WiFi
const char * ssid = "Zaimobil";
const char * wifi_password = "holaputa";
// MQTT
const char* mqtt_server = "192.168.146.53";  // IP of the MQTT broker
const char* humidity_topic = "home/livingroom/humidity";
const char* temperature_topic = "home/mov/tab";
const char* mqtt_username = "pi"; // MQTT username
const char* mqtt_password = "1234"; // MQTT password
const char* clientID = "client_livingroom"; // MQTT client ID

// Initialise the WiFi and MQTT Client objects
WiFiClient wifiClient;
// 1883 is the listener port for the Broker
PubSubClient client(mqtt_server, 1883, wifiClient); 


// Custom function to connet to the MQTT broker via WiFi
void connect_MQTT(){
  Serial.print("Connecting to ");
  Serial.println(ssid);

  // Connect to the WiFi
  WiFi.begin(ssid, wifi_password);

  // Wait until the connection has been confirmed before continuing
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  // Debugging - Output the IP Address of the ESP8266
  Serial.println("WiFi connected");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  // Connect to MQTT Broker
  // client.connect returns a boolean value to let us know if the connection was successful.
  // If the connection is failing, make sure you are using the correct MQTT Username and Password (Setup Earlier in the Instructable)
  if (client.connect(clientID, mqtt_username, mqtt_password)) {
    Serial.println("Connected to MQTT Broker!");
  }
  else {
    Serial.println("Connection to MQTT Broker failed...");
  }
}


void setup() {
  Serial.begin(115200);
 
  M5.begin(true, false, true);
  M5.Lcd.setTextSize(3);
  Serial.println("HX711 programa de calibracion");
  Serial.println("Quita cualquier peso de la bascula");
  Serial.println("Una vez empiece a mostrar infqormacion de medidas, coloca un peso conocido encima de la bascula");

  // Suscribirse al tópico MQTT especificado
  client.subscribe(MQTT_TOPIC);
  // Iniciar sensor
  bascula.begin(pinData, pinClk);
 
  // Aplicar la calibración
  bascula.set_scale();
  // Iniciar la tara
  // No tiene que haber nada sobre el peso
  bascula.tare();
 
  // Obtener una lectura de referencia
  long zero_factor = bascula.read_average();
  // Mostrar la primera desviación
  Serial.print("Zero factor: ");
  Serial.println(zero_factor);
     connect_MQTT();
}

 
void loop() {
 

  
  delay(500);
}
