#include <M5Stack.h>
#include <SPI.h>      // includes SPI bus library
#include <MFRC522.h>      // includes specific library for MFRC522

#define RST_PIN  22      // constant to reference reset pin
#define SS_PIN  5      // constant to reference slave select pin

MFRC522 mfrc522(SS_PIN, RST_PIN); // creates mfrc522 object sending slave select and reset pins

byte LecturaUID[4];         // creates array to store the read UID
byte Usuario1[4] = {0xD0, 0x20, 0xD7, 0x25} ;   // Card UID
byte Usuario2[4] = {0x16 , 0x33, 0x63, 0xD3} ;   // Card UID

void setup() {
  M5.begin(); // initialises M5Stack
  M5.lcd.setTextSize(2.5);
  SPI.begin();        // initialises SPI bus
  mfrc522.PCD_Init();     // initialise reader module
  Serial.println("Listo");    
}

void loop() {
  if ( ! mfrc522.PICC_IsNewCardPresent())   // if no card is present
    return;           // returns to the loop waiting for a card

  if ( ! mfrc522.PICC_ReadCardSerial())     // if you cannot obtain data from the card
    return;           // returns to the loop waiting for another card

  Serial.print("UID:");  
  for (byte i = 0; i < mfrc522.uid.size; i++) { // loop traverses one byte at a time through the UID
    if (mfrc522.uid.uidByte[i] < 0x10) {  // if the byte read is less than 0x10
      Serial.print(" 0");       // prints blank and zero number
    }
    else {          // but
      Serial.print(" ");        // prints a blank space
    }
    Serial.print(mfrc522.uid.uidByte[i], HEX);    // prints the byte of the UID read in hexadecimal
    LecturaUID[i] = mfrc522.uid.uidByte[i];   // stores the byte of the UID read into array
  }

  Serial.print("\t");         

  if (comparaUID(LecturaUID, Usuario1))   // call function compareUID with User1
    M5.Lcd.print("Bienvenido Arnau \n");// if it returns true it displays welcome text
  else if (comparaUID(LecturaUID, Usuario2)) // call function compareUID with User2
    M5.Lcd.print("Bienvenido Zaida \n"); // if it returns true it displays welcome text
  else           // if false
    M5.Lcd.print("No te conozco \n");    // shows text equivalent to access denied

  mfrc522.PICC_HaltA();     // stops card communication
}

boolean comparaUID(byte lectura[], byte usuario[]) // function compareUID
{
  for (byte i = 0; i < mfrc522.uid.size; i++) { // loop traverses one byte at a time through the UID
    if (lectura[i] != usuario[i])       // if byte of UID read is different from user
      return (false);         // returns false
  }
  return (true);          // if all 4 bytes match returns true
}
