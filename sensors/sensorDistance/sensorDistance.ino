
#define BLANCO 0XFFFF
#define NEGRO 0
#define AZUL 0x001F
#include <M5Stack.h>

//Asignamos los pines para el trigger y el echo
const int EchoPin = 5;
const int TriggerPin = 2;
const int Led =16 ;

void setup() {
  //inicializamos el m5 y el puerto serie, asignamos los pines de output e input
  
   M5.begin(true, false, true);
   M5.Lcd.setTextSize(3);
   Serial.begin(9600);
   pinMode(TriggerPin, OUTPUT);
   pinMode(EchoPin, INPUT);
   pinMode(Led, OUTPUT);
    pinMode(Led, LOW);
 
}

void loop() {
   M5.Lcd.setCursor(0, 10);
   
   int cm = ping(TriggerPin, EchoPin);  //Calculamos a cuantos cm está el objetivo
   Serial.print("Distancia: ");
   Serial.print(cm);

   if(cm>50){ //Si el objetivo está lejos, el m5 se para y si está cerca, saluda.
      
       digitalWrite(Led, LOW);
       M5.Lcd.fillRect (0, 100, 300, 150, BLACK); 
       M5.Lcd.print ("Hasta luego!");
       delay(1000);
       
    }else{
        digitalWrite(Led, HIGH);
       M5.Lcd.fillRect (0, 100, 300, 150, BLUE); 
       M5.Lcd.print ("Hola usuario!");
         delay(1000);
    }
    
   delay(500);
}

//calcula la distancia a la que está el objetivo
int ping(int TriggerPin, int EchoPin) {
   long duration, distanceCm;
   
   digitalWrite(TriggerPin, LOW);  //para generar un pulso limpio ponemos a LOW 4us
   delayMicroseconds(4);
   digitalWrite(TriggerPin, HIGH);  //generamos Trigger (disparo) de 10us
   delayMicroseconds(10);
   digitalWrite(TriggerPin, LOW);
   
   duration = pulseIn(EchoPin, HIGH);  //medimos el tiempo entre pulsos, en microsegundos
   
   distanceCm = duration * 10 / 292/ 2;   //convertimos a distancia, en cm
   return distanceCm;
}
