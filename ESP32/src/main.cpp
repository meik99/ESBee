#include "control.h"
#include "bluetooth.h"

#define ONE_HOUR_IN_MILLIS /*(10*1000)*/ (1 * 60 * 60 * 1000)
#define FIVE_MINUTES_IN_MILLIS /*(1*1000)*/ (5 * 60 * 1000)

long lastPumpCycle = -ONE_HOUR_IN_MILLIS-1000;
long lastSensorReadingsTime = -ONE_HOUR_IN_MILLIS-1000;
long fanStartTime = 0;
SensorReadings lastSensorReadings;
int pumpPowerDivisor = 1;

int getPumpDivisor(float temperature) {
    if (temperature <= 10.0f) {
        return 1000;
    } else if(temperature <= 15.0f) {
        return 3;
    } else if(temperature <= 20.0f) {
        return 2;
    } else if(temperature <= 25.0f) {
        return 1;
    } else if(temperature <= 30.0f) {
        return 2;
    }
    return 1000;
}

float getPumpAmount(float temperature) {
    if (temperature <= 10.0f) {
        return 0.0f;
    } else if(temperature <= 15.0f) {
        return 0.5f;
    } else if(temperature <= 20.0f) {
        return 1.0f;
    } else if(temperature <= 25.0f) {
        return 1.5f;
    } else if(temperature <= 30.0f) {
        return 1.0f;
    }

    return 0.0f;
}

bool mustContinuePumping(float temperature) {
    return getPump()->pumpedMilliliter(millis()) < getPumpAmount(temperature);
}

void setup() {
    Serial.begin(115200);

    while (!Serial) {
        delay(100);
    }

    setupControl();
    initBleServer();
}

void loop() {
    if (millis() - lastSensorReadingsTime >= ONE_HOUR_IN_MILLIS) {
        SensorReadings sensorReadings = readHumidityAndTemperature();
        lastSensorReadings = sensorReadings;
        setTemperature(sensorReadings.temperature);
        setHumidity(sensorReadings.humidity);
        lastSensorReadingsTime = millis();
    }

    if (getPumpControl() == PUMP_ON) {
        getPump()->start(millis());
    } else if (getPumpControl() == PUMP_OFF) {
        getPump()->stop();
    }

    if (millis() - lastPumpCycle >= ONE_HOUR_IN_MILLIS) {
        getPump()->start(millis());

        while(mustContinuePumping(lastSensorReadings.temperature)) {
            delay(100);
        }

        if(getPumpControl() == PUMP_OFF) {
            getPump()->stop();
        }

        lastPumpCycle = millis();
        fanStartTime = millis();
    }

    if (millis() - fanStartTime < FIVE_MINUTES_IN_MILLIS) {
        getRealIO()->on(FAN);
    } else {
        getRealIO()->off(FAN);
    }

    delay(100);
}