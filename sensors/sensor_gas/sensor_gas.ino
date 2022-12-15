#define         MQ1                       (36)     //define la entrada analogica para el sensor
#define         RL_VALOR             (5)     //define el valor de la resistencia mde carga en kilo ohms
#define         RAL       (9.83)  // resistencia del sensor en el aire limpio / RO, que se deriva de la   tabla de la hoja de datos
#define         GAS_LP                      (0)

#define BLANCO 0XFFFF
#define NEGRO 0
#define ROJO 0xF800
#define VERDE 0x07E0
#define AZUL 0x001F
#include <M5Stack.h>

String inputstring = "";                                                        //Cadena recibida desde el PC
float           LPCurve[3]  =  {2.3,0.21,-0.47};
float           Ro           =  10;
//humidity & temp
#include "DHT.h"

#define DHTPIN 2     // Pin del sensor DHT en el que está conectado
#define DHTTYPE DHT11   // Tipo de sensor DHT

DHT dht(DHTPIN, DHTTYPE);

//Toma 30 medidas para calibrar el sensor
 float Calibracion(float mq_pin){
  int i;
  float val=0;
    for (i=0;i<30;i++) {                 
    val += calc_res(analogRead(mq_pin));
    delay(300);
  }
  val = val/30;                     //calcular el valor medio
  val = val/RAL;
  return val;
}



void setup(){
  M5.begin(true, false, true);
  M5.Lcd.setTextSize(3);
  M5.Lcd.printf("Calibrando...");
Serial.begin(9600);    //Inicializa Serial a 9600 baudios
 dht.begin();


 Serial.println("Iniciando ...");
   //configuracion del sensor
  Serial.print("Calibrando...\n");
  Ro = Calibracion(MQ1);                        //Calibrando el sensor. Por favor de asegurarse que el sensor se encuentre en una zona de aire limpio mientras se calibra
  Serial.print("Calibracion finalizada...\n");
  Serial.print("Ro=");
  Serial.print(Ro);
  Serial.print("kohm");
  Serial.print("\n");
}

 
void loop()
{
   Serial.print("Lectura:");
   Serial.print(lecturaMQ(MQ1));
   //Serial.print(porcentaje_gas(lecturaMQ(MQ1)/Ro,GAS_LP) );
   Serial.print( "" );
   Serial.print("    ");
   Serial.print("\n");

   // Lee la humedad y la temperatura desde el sensor DHT
  float h = dht.readHumidity();
  float t = dht.readTemperature();

   // Imprime los valores de humedad y temperatura en la consola
  Serial.print("Humedad: ");
  Serial.print(dht.readHumidity());
  Serial.print("%");
  Serial.print("\t");
  Serial.print("Temperatura: ");
  Serial.print(dht.readTemperature());
  Serial.println("C");  
   
    M5.Lcd.setCursor(0, 30);
    if (lecturaMQ(MQ1)>2350.00){
       M5.Lcd.setCursor(0, 30);
       M5.Lcd.fillRect (0, 100, 300, 150, RED); 
       M5.Lcd.print ("Peligro gas detectado");
       for(int deg = 0; deg < 360; deg = deg + 1){
        dacWrite(25,int(128 + 127 * (sin (deg * PI * 4/ 180))));
       }
      
    }
    else if(lecturaMQ(MQ1)<2350.00){
       M5.Lcd.setCursor(0, 30);
       M5.Lcd.fillRect (0, 100, 300, 150, BLACK); 
       M5.Lcd.print ("Ningun gas  detectado");
 
      
    }
    
   
    delay(1000);
}//loop

//Calcula la lectura raw
float calc_res(int raw_adc)
{
  return ( ((float)RL_VALOR*(1023-raw_adc)/raw_adc));
}



 //Calcula la lectura del sensor
float lecturaMQ(int mq_pin){
  
  float res=analogRead(mq_pin);
  return res;

}
