#include "control.h"
#include "bluetooth.h"
#include "log.h"

#define ONE_SECOND 1000
#define ONE_MINUTE (60 * ONE_SECOND)
#define ONE_HOUR (60 * ONE_MINUTE)
#define FIFTEEN_MINUTES (15 * ONE_MINUTE)

#define N_LINES 512
#define N_CHARACTERS 512

long lastPumpCycle = 0;
long lastSensorReadings = -ONE_HOUR;
long fanStartTime = 0;
long lastLogDump = -ONE_MINUTE;

bool isBelowFormicAcidMeltingPoint = false;
bool logPump = true;

std::string lastPumpControl = "";

Log logger;

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
    if (millis() - lastSensorReadings >= ONE_HOUR) {
        logger.i("reading humidity sensor, last read was at " + std::to_string(lastSensorReadings) + " current time is " +
              std::to_string(millis()));

        SensorReadings sensorReadings = readHumidityAndTemperature();

        if (sensorReadings.temperature - -1.0f < 0.01f) {
            logger.e("failed to read temperature");
        }
        if (sensorReadings.humidity - -1.0f < 0.01f) {
            logger.e("failed to read humidity");
        }
        logger.i("reported temperature was " + std::to_string(sensorReadings.temperature) + "°C");
        logger.i("reported humidity was " + std::to_string(sensorReadings.temperature) + "%");

        isBelowFormicAcidMeltingPoint = sensorReadings.temperature < 8.4f;
        lastSensorReadings = millis();

        setTemperature(sensorReadings.temperature);
        setHumidity(sensorReadings.humidity);
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
        delay(1000);

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

    if (millis() - fanStartTime < FIFTEEN_MINUTES) {
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