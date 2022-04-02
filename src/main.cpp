//
// Created by michael on 4/3/22.
//

#include "Arduino.h"
#include "WiFi.h"
#include "BluetoothSerial.h"
#include "wifi_credentials.h"

void setup() {
    Serial.begin(115200);

    while (!Serial) {
        delay(100);
    }

    Serial.println("Starting");

    WiFi.mode(WIFI_STA);
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

    Serial.print("Connecting to WiFi ..");

    while (WiFi.status() != WL_CONNECTED) {
        Serial.print('.');
        delay(1000);
    }

    Serial.println(WiFi.localIP());
}

void loop() {

}