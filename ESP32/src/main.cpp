#include <Arduino.h>
#include <pump.h>

#define PUMP_RELAY 12

void setup() {
    Serial.begin(115200);

    pinMode(PUMP_RELAY, OUTPUT);
}

void loop() {
    // On
    Serial.println("High");
    digitalWrite(PUMP_RELAY, HIGH);
    delay(1000);

    // Off
    Serial.println("Low");
    digitalWrite(PUMP_RELAY, LOW);
    delay(1000);
}