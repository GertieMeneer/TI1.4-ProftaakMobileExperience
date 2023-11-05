#include <PubSubClient.h>
#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#include <WiFiUdp.h>
#include <WiFiClientSecure.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <Adafruit_GFX.h>
#include "Adafruit_LEDBackpack.h"

#ifndef _BV
#define _BV(bit) (1<<(bit))
#endif

Adafruit_LEDBackpack matrix = Adafruit_LEDBackpack();
LiquidCrystal_I2C lcd(0x27, 16, 2);

uint8_t counter = 0;
bool user = false;

const int trigPin = 5;
const int echoPin = 18;
const int onboardLed = 2;

String username;

const float SOUND_SPEED = 0.034;

long duration = 0;
float distanceCm = 0;
String matrixMessage = "";

const char* ssid = "joost";
const char* password = "patrick123";

const char* mqttServer = "7b10c1a6effd49c798757d01597a1663.s2.eu.hivemq.cloud";
const int mqttPort = 8883;
const char* mqttUser = "espSub";
const char* mqttPassword = "espPasswordH3elZiek";

WiFiClientSecure wifiClient;
PubSubClient mqttClient(wifiClient);

void setup() {
  matrix.begin(0x76);  // pass in the address
  lcd.begin();
  lcd.backlight();
  Serial.begin(115200);

  // Connect to Wi-Fi
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("Tryin' to connect. . .");
    delay(1000);
  }
  Serial.println("Connected");

  wifiClient.setInsecure();

  // Set up MQTT client
  mqttClient.setServer(mqttServer, mqttPort);
  mqttClient.setCallback(callback);
  mqttClient.setKeepAlive(60);

  while (!mqttClient.connected()) {
    if (mqttClient.connect("esp", mqttUser, mqttPassword)) {
      Serial.println("mqtt broker connected");
    } else {
      Serial.print("failed with state ");
      Serial.print(mqttClient.state());
      delay(2000);
    }
  }
  mqttClient.subscribe("topic/matrix");
  mqttClient.subscribe("topic/username");

  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);

  pinMode(onboardLed, OUTPUT);
}

void loop() {
  mqttClient.loop();

  // Move the picture one pixel to the left
  for (uint8_t i = 0; i < 8; i++) {
    // Shift the LED values to the left
    matrix.displaybuffer[i] <<= 1;
  }

  checkUltrasoon();

  if (distanceCm < 80) {
    person();
    if (matrixMessage != "") {
      clearMatrix();
      if (matrixMessage == "010203070809121314161824252730323340434445485052545659606164") {
        correctInput();
        lcd.clear();
      }
      else {
        // make whole led matrix RED and after clear
        wrongInput();
        lcd.clear();
      }
      matrixMessage = "";
    }
  }
  else {
    lcd.clear();
    clearMatrix();
  }
  Serial.print("Distance (cm): ");
  Serial.println(distanceCm);
}

void callback(char* topic, byte* payload, unsigned int length) {
  // handle incoming message from mqtt hivemq
  String message = "";
  String TopicString = String(topic);

  for (int i = 0; i < length; i++) {
    message += (char)payload[i];
  }

  //localMessage = message;
  Serial.println("Message received on topic " + String(topic) + ": " + message);

  if (TopicString == "topic/username") {
    Serial.println("username");
    username = message;
    user = true;
  } else {
    matrixMessage = message;
  }
}

void turnOnLED(uint8_t ledNumber) {
  // Validate the input range
  if (ledNumber < 1 || ledNumber > 64) {
    return; // Invalid LED number, do nothing
  }

  // Calculate the row and column indices of the LED
  uint8_t row = (ledNumber - 1) / 8;
  uint8_t col = (ledNumber - 1) % 8;

  // Set the corresponding LED in the display buffer to 1
  matrix.displaybuffer[row] |= (1 << col);

  // Write the changes to the display
  matrix.writeDisplay();
}

void turnOnLEDs(uint8_t ledNumbers[], uint8_t numLEDs) {
  // Iterate through each LED number in the array
  for (uint8_t i = 0; i < numLEDs; i++) {
    uint8_t ledNumber = ledNumbers[i];

    // Validate the input range
    if (ledNumber < 1 || ledNumber > 64) {
      // Invalid LED number, skip to the next iteration
      continue;
    }

    // Calculate the row and column indices of the LED
    uint8_t row = (ledNumber - 1) / 8;
    uint8_t col = (ledNumber - 1) % 8;

    // Set the corresponding LED in the display buffer to 1
    matrix.displaybuffer[row] |= (1 << col);
  }

  // Write the changes to the display
  matrix.writeDisplay();
}

void checkUltrasoon() {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);

  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  duration = pulseIn(echoPin, HIGH);

  distanceCm = duration * SOUND_SPEED / 2;
}

void person() {
  if(!user) {
    showCode();
  } else {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Welkom");
    lcd.setCursor(0, 1);
    lcd.print(username);
  }
  digitalWrite(onboardLed, HIGH);

  // paint one LED per row. The HT16K33 internal memory looks like
  // a 8x16 bit matrix (8 rows, 16 columns)
  for (uint8_t i = 0; i < 8; i++) {
    // draw a diagonal row of pixels
    matrix.displaybuffer[i] = _BV((counter + i) % 16) | _BV((counter + i + 8) % 16);
  }
  // write the changes we just made to the display
  matrix.writeDisplay();
  delay(100);

  counter++;
  if (counter >= 16) counter = 0;
}

void showCode() {
  lcd.setCursor(0, 0);
  lcd.print("De code is:");
  lcd.setCursor(0, 1);
  lcd.print("7385");
}

void clearMatrix() {
  for (uint8_t i = 0; i < 8; i++) {
    matrix.displaybuffer[i] = 0;
  }
  // write the changes we just made to the display
  matrix.writeDisplay();
  digitalWrite(onboardLed, LOW);
}

void wrongInput() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Helaas, probeer");
  lcd.setCursor(0, 1);
  lcd.print("het opnieuw :(");
  Serial.println("WRONG CODE!");
  String result = "403";
  const char* resultcstr = result.c_str();
  mqttClient.publish("topic/result", resultcstr);

  int numFlashes = 0;
  // Flash the matrix three times
  while (numFlashes < 3) {
    // Turn off the matrix LEDs
    for (uint8_t i = 0; i < 8; i++) {
      matrix.displaybuffer[i] = 0;
    }
    // write the changes we just made to the display
    matrix.writeDisplay();
    delay(500);

    // Fill the matrix
    for (uint8_t i = 0; i < 8; i++) {
      matrix.displaybuffer[i] = 0xFF;
    }
    // write the changes we just made to the display
    matrix.writeDisplay();
    delay(500);

    numFlashes++;
  }
}

void correctInput() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Het is goed!");
  lcd.setCursor(0, 1);
  lcd.print("Goed gedaan! :)");
  String result = "200";
  const char* resultcstr = result.c_str();
  mqttClient.publish("topic/result", resultcstr);

  Serial.println("CORRECT CODE!");

  for (int i = 0; i < 3; i++) {
    uint8_t leds[] = {8, 16, 32, 40, 1, 2, 6, 7, 11, 12, 13, 15, 17, 23, 26, 29, 31, 39, 42, 43, 44, 47, 49, 51, 53, 55, 58, 59, 60, 63};
    uint8_t numLEDs = 30;
    turnOnLEDs(leds, numLEDs);
    delay(1000);
    clearMatrix();
    delay(200);
  }
  balloon();
  user = false;
}

void balloon() {
  uint8_t leds[] = {59, 60, 61};
  uint8_t numLEDs = 3;
  turnOnLEDs(leds, numLEDs);
  delay(200);
  clearMatrix();
  delay(200);

  uint8_t leds2[] = {58, 59, 60, 61, 62, 51, 52, 53};
  uint8_t numLEDs2 = 8;
  turnOnLEDs(leds2, numLEDs2);
  delay(200);
  clearMatrix();
  delay(200);

  uint8_t leds3[] = {63, 62, 61, 60, 59, 51, 52, 53, 54, 55, 46, 45, 44};
  uint8_t numLEDs3 = 13;
  turnOnLEDs(leds3, numLEDs3);
  delay(200);
  clearMatrix();
  delay(200);

  uint8_t leds4[] = {63, 62, 61, 60, 59, 51, 52, 53, 54, 55, 47, 46, 45, 44, 43, 36, 37, 38};
  uint8_t numLEDs4 = 18;
  turnOnLEDs(leds4, numLEDs4);
  delay(200);
  clearMatrix();
  delay(200);

  uint8_t leds5[] = {61, 60, 59, 50, 51, 52, 53, 54, 42, 43, 44, 45, 46, 34, 35, 36, 37, 38, 27, 28, 29};
  uint8_t numLEDs5 = 21;
  turnOnLEDs(leds5, numLEDs5);
  delay(200);
  clearMatrix();
  delay(200);

  uint8_t leds6[] = {59, 60, 61, 51, 52, 53, 42, 43, 44, 45, 46, 34, 35, 36, 37, 38, 26, 27, 28, 29, 30, 19, 20, 21};
  uint8_t numLEDs6 = 24;
  turnOnLEDs(leds6, numLEDs6);
  delay(200);
  clearMatrix();
  delay(200);

  uint8_t leds7[] = {61, 52, 53, 54, 44, 45, 46, 35, 36, 37, 38, 39, 27, 28, 29, 30, 31, 19, 20, 21, 22, 23, 12, 13, 14};
  uint8_t numLEDs7 = 25;
  turnOnLEDs(leds7, numLEDs7);
  delay(200);
  clearMatrix();
  delay(200);

  uint8_t leds8[] = {62, 61, 55, 52, 44, 43, 45, 36, 35, 37, 27, 26, 28, 29, 30, 18, 19, 20, 21, 22, 10, 11, 12, 13, 14, 3, 4, 5};
  uint8_t numLEDs8 = 28;
  turnOnLEDs(leds8, numLEDs8);
  delay(200);
  clearMatrix();
  delay(200);
}
