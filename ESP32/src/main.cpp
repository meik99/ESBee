#include "control.h"
#include "bluetooth.h"
#include "log.h"

#define ONE_SECOND 1000
#define ONE_MINUTE (60 * ONE_SECOND)
#define ONE_HOUR (60 * ONE_MINUTE)
#define FIFTEEN_MINUTES (15 * ONE_MINUTE)

#define N_LINES 512
#define N_CHARACTERS 512

long lastPumpCycle = -ONE_HOUR;
long lastSensorReadingsTime = -ONE_HOUR;
long lastPumpCycle = 0;
long fanStartTime = 0;
long lastLogDump = -ONE_MINUTE;

bool logPump = true;

std::string lastPumpControl = "";

Log logger;
SensorReadings lastSensorReadings;

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
    if (millis() - lastSensorReadingsTime >= ONE_HOUR) {
        logger.i("reading humidity sensor, last read was at " + std::to_string(lastSensorReadings) + " current time is " +
              std::to_string(millis()));
        SensorReadings sensorReadings = readHumidityAndTemperature();
        lastSensorReadings = sensorReadings;

        if (sensorReadings.temperature - -1.0f < 0.01f) {
            logger.e("failed to read temperature");
        }
        if (sensorReadings.humidity - -1.0f < 0.01f) {
            logger.e("failed to read humidity");
        }
        logger.i("reported temperature was " + std::to_string(sensorReadings.temperature) + "°C");
        logger.i("reported humidity was " + std::to_string(sensorReadings.temperature) + "%");

        setTemperature(sensorReadings.temperature);
        setHumidity(sensorReadings.humidity);
        lastSensorReadingsTime = millis();
    }

    if (lastPumpControl != getPumpControl()) {
        lastPumpControl = getPumpControl();
        if (lastPumpControl == PUMP_ON) {
            logger.i("pump was started by bluetooth characteristic");
        } else {
            logger.i("pump was stopped by bluetooth characteristic");
        }
    }

    if (getPumpControl() == PUMP_ON && !getPump()->isPumping()) {
        getPump()->start(millis());
    } else if (getPumpControl() == PUMP_OFF) {
        getPump()->stop();
    }

    if (millis() - lastPumpCycle >= ONE_HOUR) {
        logger.i("starting pump cycle, last cycle was at " + std::to_string(lastPumpCycle) + " current time is " +
              std::to_string(millis()));
        getPump()->start(millis());

        while(mustContinuePumping(lastSensorReadings.temperature)) {
            delay(100);
        }

        // pump should not be stopped if it is currently overwritten by bluetooth controls
        if (getPumpControl() == PUMP_OFF) {
            logger.i("stopping pump cycle at " + std::to_string(millis()));
            getPump()->stop();
        } else {
            logger.w("pump cycle was not stopped at " + std::to_string(millis()) +
                  " due to overruling bluetooth controls");
        }

        lastPumpCycle = millis();
        fanStartTime = millis();
    }

    if (millis() - fanStartTime < ONE_MINUTE) {
        logger.i("starting fan at " + std::to_string(millis()));
        getRealIO()->on(FAN);
    } else {
        logger.i("stopping fan at " + std::to_string(millis()));
        getRealIO()->off(FAN);
    }

    if(millis() - lastLogDump >= ONE_MINUTE) {
        Serial.println(logger.toJSONMessage().c_str());
        lastLogDump = millis();
    }

    delay(100);
}