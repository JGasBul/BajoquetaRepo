#include <M5Stack.h>

const int MQ_PIN = 36;
const int MQ_DELAY = 1000;
bool pressed = false;

void setup()
{
  M5.begin(true, false, true);
  M5.Lcd.setTextSize(2);
  Serial.begin(9600);
}
void loop()
{
  M5.update();
  M5.Lcd.setCursor(0, 16);
  int val = analogRead(MQ_PIN);
  Serial.println(val);

  M5.Lcd.println("Detecting");
  M5.Lcd.fillRect(0, 100, 300, 150, GREEN);

  if (val >= 400 && pressed == false) {
    M5.Lcd.println("Warning");
    M5.Lcd.fillRect(0, 100, 300, 150, RED);
    for (int deg = 0; deg < 360 ; deg = deg + 1) {
      dacWrite(25,int(128 + 127 * (sin(deg*PI*4/180))));
    }
    delay(500);

    if (M5.BtnA.wasPressed()) {
      pressed = true;
      M5.Lcd.println("Detecting");
      M5.Lcd.fillRect(0, 100, 300, 150, GREEN);

    }
  }

  delay(MQ_DELAY);
}
