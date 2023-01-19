#define BLANCO 0XFFFF
#define NEGRO 0
#define AZUL 0x001F
#include <M5Stack.h>

//Asignamos los pines para el trigger y el echo
const int EchoPin = 5;
const int TriggerPin = 2;
const int Led = 16 ;

//Constante que contendrá la distancia en cm
 long DistanciaCm;
//Declaracion manejadores de los semaforos binarios
SemaphoreHandle_t xSemaphore1 = NULL;
SemaphoreHandle_t xSemaphore2 = NULL;
SemaphoreHandle_t xSemaphore3 = NULL;
//---------------------------------------------------------------------------------------------------------
void tarea1(void *pvParameter)
{
  while (1) {
    Serial.println("Ejecutando tarea 1");
    M5.Lcd.setCursor(0, 10);
    xSemaphoreGive(xSemaphore1);
    vTaskDelay(100 / portTICK_PERIOD_MS);
  }
}
//---------------------------------------------------------------------------------------------------------
void tarea2(void *pvParameter)
{
  while (1) {
    if ( xSemaphore1 != NULL )
    {
      if (xSemaphoreTake( xSemaphore1, portMAX_DELAY) == pdTRUE)
      {
        Serial.println("Ejecutando tarea 2, calculo de la distancia");
        long duration;

        digitalWrite(TriggerPin, LOW);  //para generar un pulso limpio ponemos a LOW 4us
        delayMicroseconds(4);
        digitalWrite(TriggerPin, HIGH);  //generamos Trigger (disparo) de 10us
        delayMicroseconds(10);
        digitalWrite(TriggerPin, LOW);

        duration = pulseIn(EchoPin, HIGH);  //medimos el tiempo entre pulsos, en microsegundos

        DistanciaCm = duration * 10 / 292 / 2;  //convertimos a distancia, en cm
        Serial.print(DistanciaCm);
        xSemaphoreGive(xSemaphore2);
      }
    }
  }
}
//---------------------------------------------------------------------------------------------------------
void tarea3(void *pvParameter)
{
  while (1) {
    if ( xSemaphore2 != NULL )
    {
      if (xSemaphoreTake( xSemaphore2, portMAX_DELAY) == pdTRUE)
      {
        Serial.println("Ejecutando tarea 3");
        //Lectura del valor
        Serial.print("Distancia: ");
        Serial.print(DistanciaCm);
        xSemaphoreGive(xSemaphore3);
      }
    }
  }
}
//---------------------------------------------------------------------------------------------------------
void tarea4(void *pvParameter)
{
  while (1) {
    if ( xSemaphore3 != NULL )
    {
      if (xSemaphoreTake(xSemaphore3, portMAX_DELAY) == pdTRUE)
      {
        Serial.println("Ejecutando tarea 4, comprobación de distancia al objetivo");

        if (DistanciaCm > 50) { //Si el objetivo está lejos, el m5 se para y si está cerca, saluda.

          digitalWrite(Led, LOW);
          M5.Lcd.fillRect (0, 100, 300, 150, BLACK);
          M5.Lcd.print ("Hasta luego!");
          delay(100);

        } else {
          digitalWrite(Led, HIGH);
          M5.Lcd.fillRect (0, 100, 300, 150, BLUE);
          M5.Lcd.print ("Hola usuario!");
          delay(100);
        }
      }
    }
  }
}
//---------------------------------------------------------------------------------------------------------
void setup() {
  M5.begin(true, false, true);
  M5.Lcd.setTextSize(3);
  Serial.begin(9600);
  pinMode(TriggerPin, OUTPUT);
  pinMode(EchoPin, INPUT);
  pinMode(Led, OUTPUT);
  pinMode(Led, LOW);

   // 1º) Creacion semaforos binarios
    xSemaphore1 = xSemaphoreCreateBinary();
    xSemaphore2 = xSemaphoreCreateBinary();
    xSemaphore3 = xSemaphoreCreateBinary();
       
   // 2º) Creacion de tareas
    xTaskCreate(&tarea1, "tarea1", 1024*8, NULL, 1, NULL);
    xTaskCreate(&tarea2, "tarea2", 1024*8, NULL, 1, NULL);
    xTaskCreate(&tarea3, "tarea3", 1024*8, NULL, 1, NULL);
    xTaskCreate(&tarea4, "tarea4", 1024*8, NULL, 1, NULL);
}
//---------------------------------------------------------------------------------------------------------
void loop() {}
