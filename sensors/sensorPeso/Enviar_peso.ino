#include "HX711.h"
#include <M5Stack.h>
using namespace std;
#include "WiFi.h"
#include "AsyncUDP.h"
#include "PubSubClient.h" // Connect and publish to the MQTT broker


AsyncUDP udp;

// Pin de datos y de reloj
byte pinData = 16;
byte pinClk = 5;
 
HX711 bascula;
 
// Parámetro para calibrar el peso y el sensor
float factor_calibracion = 24550.0; //Factor de calibración 
//20780.0

//pin pulsador
const int PULSADOR = 19;

// Reemplazar estos valores con los detalles de su servidor MQTT
const char* MQTT_SERVER = "192.168.194.53";
const int MQTT_PORT = 1883;

// Reemplazar este valor con el tópico al que desea suscribirse
const char* MQTT_TOPIC = "home/mov/tab";

// WiFi
const char * ssid = "Zaimobil";
const char * wifi_password = "holaputa";
// MQTT
const char* mqtt_server = "192.168.194.53";  // IP of the MQTT broker
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

  // PULSADOR configurado como entrada de datos
    pinMode(PULSADOR,INPUT);
 
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
  //char hora[3];
  //sprintf("hola")
  M5.Lcd.setCursor(0, 10);
  M5.Lcd.fillRect (0, 100, 300, 150, BLACK); 
  // Aplicar calibración
  bascula.set_scale(factor_calibracion);
 
  // Mostrar la información para ajustar el factor de calibración
  Serial.print("Leyendo: ");
  Serial.print(bascula.get_units(), 1);

 
  Serial.print(bascula.get_units());
  Serial.print(" kgs");
  Serial.print(" factor_calibracion: ");
  Serial.print(factor_calibracion);
  Serial.println();
  
  
   // MQTT can only transmit strings
  //float dato=(bascula.get_units(), 3);
  float dato=bascula.get_units();
    if(dato<0){
    dato=0;
   }
     M5.Lcd.print (dato);
      M5.Lcd.print("gr.");
  String peso=""+String((float)dato)+" kg ";

// PUBLISH to the MQTT Broker (topic = Temperature, defined at the beginning)
  if (client.publish(temperature_topic, String(peso).c_str())) {
    Serial.println("Weight sent!");
  }
  // Again, client.publish will return a boolean value depending on whether it succeded or not.
  // If the message failed to send, we will try again, as the connection may have broken.
  else {
    Serial.println("Temperature failed to send. Reconnecting to MQTT Broker and trying again");
    client.connect(clientID, mqtt_username, mqtt_password);
    delay(10); // This delay ensures that client.publish doesn't clash with the client.connect call
    client.publish(temperature_topic, String(peso).c_str());
  }

  
    //Si se pulsa el botón se recalibra la báscula
  if(!digitalRead(PULSADOR)){
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
 
   bascula.set_scale(factor_calibracion);
  } 
 
  delay(500);
}
