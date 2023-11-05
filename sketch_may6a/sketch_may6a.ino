#include <PubSubClient.h>
#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#include <WiFiUdp.h>
#include <WiFiClientSecure.h>

const char* ssid = "xxx"; 
const char* password = "xxx"; 

const char* mqttServer = "7b10c1a6effd49c798757d01597a1663.s2.eu.hivemq.cloud"; 
const int mqttPort = 8883; 
const char* mqttUser = "espSub";
const char* mqttPassword = "espPasswordH3elZiek"; 

WiFiClientSecure wifiClient;
PubSubClient mqttClient(wifiClient); 

void setup() {
  Serial.begin(115200);

  // Connect to Wi-Fi 
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) { 
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
  mqttClient.subscribe("topic/test");
} 

void loop() { 
  mqttClient.loop(); 
} 

void callback(char* topic, byte* payload, unsigned int length) { 
  // Handle incoming message 
  String message = ""; 

  for (int i = 0; i < length; i++) { 
    message += (char)payload[i]; 
  } 

  Serial.println("Message received on topic " + String(topic) + ": " + message); 
} 