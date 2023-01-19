#include <M5Stack.h>
using namespace std;
#include "WiFi.h";
#include "AsyncUDP.h"
#include "PubSubClient.h" // Connect and publish to the MQTT broker

AsyncUDP udp;

const int ledPin = 5;

//detalles del servidor MQTT
const char* MQTT_SERVER = "192.168.194.53";
const int MQTT_PORT = 1883;

// Reemplazar este valor con el tópico al que desea suscribirse
const char* MQTT_TOPIC = "home/led";

// WiFi
const char * ssid = "Zaimobil";
const char * wifi_password = "holaputa";
// MQTT
const char* mqtt_server = "192.168.194.53";  // IP of the MQTT broker
const char* mqtt_username = "pi"; // MQTT username
const char* mqtt_password = "1234"; // MQTT password
const char* clientID = "LED"; // MQTT client ID

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
     client.setCallback(callback);
  }
  else {
    Serial.println("Connection to MQTT Broker failed...");
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
    M5.lcd.printf("he recibido");
  }
  Serial.println();
 
  // Switch on the LED if an 1 was received as first character
  if ((char)payload[0] == '0') {
    digitalWrite(ledPin, LOW);   // Turn the LED on (Note that LOW is the voltage level
    // but actually the LED is on; this is because
    // it is acive low on the ESP-01)
  } else {
    digitalWrite(ledPin, HIGH);  // Turn the LED off by making the voltage HIGH
  }
 
}

//SETUP
void setup() {
  
  pinMode(ledPin, OUTPUT);
  M5.begin(true, false, true);
  Serial.begin(115200);
// Suscribirse al tópico MQTT especificado
 // client.subscribe(MQTT_TOPIC);
   connect_MQTT();
    client.setCallback(callback);
}

void loop() {
    client.subscribe(MQTT_TOPIC);
 client.loop();

}
