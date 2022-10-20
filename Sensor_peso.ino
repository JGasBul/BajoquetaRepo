#include "HX711.h"
#include <M5Stack.h>
// Pin de datos y de reloj
byte pinData = 16;
byte pinClk = 5;
 
HX711 bascula;
 
// Parámetro para calibrar el peso y el sensor
float factor_calibracion = 24550.0; //Factor de calibración 
//20780.0
//media de las ultimas 5 medidas
 int media(){
  int medidas[5]={0,0,0,0,0};
  int res=0;
  int medida=0;
  
  for(int i=0; i<5; i++){
    medida=(bascula.get_units(), 5);
     medidas[i]=medida;
  }
  for(int i=0; i<5; i++){
     res=res+medidas[i];
  }

  Serial.println(res);
  Serial.println(res/5);
  return res/5;
}

void setup() {
  Serial.begin(9600);
  M5.begin(true, false, true);
  M5.Lcd.setTextSize(3);
  Serial.println("HX711 programa de calibracion");
  Serial.println("Quita cualquier peso de la bascula");
  Serial.println("Una vez empiece a mostrar informacion de medidas, coloca un peso conocido encima de la bascula");
 
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
}
 
void loop() {
  int res=media();
  M5.Lcd.setCursor(0, 10);
  M5.Lcd.fillRect (0, 100, 300, 150, BLACK); 
  // Aplicar calibración
  bascula.set_scale(factor_calibracion);
 
  // Mostrar la información para ajustar el factor de calibración
  Serial.print("Leyendo: ");
  Serial.print(bascula.get_units(), 1);
  Serial.print(" kgs");
  Serial.print(" factor_calibracion: ");
  Serial.print(factor_calibracion);
  Serial.println();
  
  //M5.Lcd.print (res);
   M5.Lcd.print (bascula.get_units(), 3);
  M5.Lcd.print("gr.");
  delay(200);
}
