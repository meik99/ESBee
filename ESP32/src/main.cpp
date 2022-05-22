#include "control.h"
#include "bluetooth.h"

#define ONE_HOUR_IN_MILLIS (1 * 60 * 60 * 1000)
#define FIFTEEN_MINUTES_IN_MILLIS (15 * 60 * 1000)

long lastPumpCycle = 0;
long lastSensorReadings = -ONE_HOUR_IN_MILLIS;
long fanStartTime = 0;

void setup() {
    Serial.begin(115200);

    while (!Serial) {
        delay(100);
    }

    setupControl();
    initBleServer();
    lastPumpCycle = millis();
}

void loop() {
    if (millis() - lastSensorReadings >= ONE_HOUR_IN_MILLIS) {
        SensorReadings sensorReadings = readHumidityAndTemperature();
        setTemperature(sensorReadings.temperature);
        setHumidity(sensorReadings.humidity);
        lastSensorReadings = millis();
    }

    if (getPumpControl() == PUMP_ON) {
        getPump()->start(millis());
    } else if (getPumpControl() == PUMP_OFF) {
        getPump()->stop();
    }

    if (millis() - lastPumpCycle >= ONE_HOUR_IN_MILLIS) {
        getPump()->start(millis());
        delay(1000);

        if(getPumpControl() == PUMP_OFF) {
            getPump()->stop();
        }

        lastPumpCycle = millis();
        fanStartTime = millis();
    }

    if (millis() - fanStartTime < FIFTEEN_MINUTES_IN_MILLIS) {
        getRealIO()->on(FAN);
    } else {
        getRealIO()->off(FAN);
    }

    delay(100);
}