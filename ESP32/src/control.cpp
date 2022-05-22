#include "control.h"

RealIO *realIO = new RealIO();
Pump *pump = new Pump(realIO, PUMP_RELAY);
Humidity *humidity;

void setupControl() {
    Serial.println("serial ready");
    Serial.printf("setting pump pin %d to output\n", PUMP_RELAY);

    pinMode(PUMP_RELAY, OUTPUT);

    Serial.printf("setting fan pin %d to output\n", FAN);

    pinMode(FAN, OUTPUT);

    Serial.printf("initializing humidity sensor on pin %d\n", HUMIDITY_SENSOR);
    humidity = new Humidity(new DHTRepository(HUMIDITY_SENSOR));
}
//
//void testFan(RealIO *realIO) {
//    Serial.println("testing fan");
//    realIO->on(FAN);
//    delay(5000);
//    realIO->off(FAN);
//}
//
//void testPump(RealIO *realIO) {
//    Serial.printf("initializing pump on pin %d\n", PUMP_RELAY);
//
//    Pump *pump = new Pump(realIO, PUMP_RELAY);
//
//    Serial.println("testing pump");
//
//    long time = millis();
//    pump->start(time);
//    Serial.printf("started pump at %ld\n", time);
//
//    do {
//        delay(1000);
//        time = millis();
//        Serial.printf("pumped %.3f milliliter or %.3f grams in %.3f seconds \n", pump->pumpedMilliliter(time), pump->pumpedGrams(time), time / 1000.0f);
//    } while(pump->pumpedMilliliter(time) < 2);
//
//    Serial.printf("stopping pump at %ld\n", time);
//    pump->stop();
//}

SensorReadings readHumidityAndTemperature() {
    humidity->measure();
    float humidityMeasurement = humidity->getHumidity();
    float temperatureMeasurement = humidity->getTemperature();

    if (isnan(humidityMeasurement)) {
        Serial.println("failed to read humidity");
        humidityMeasurement = -1;
    }
    if (isnan(temperatureMeasurement)) {
        Serial.println("failed to read temperature");
        temperatureMeasurement = -1;
    }
    if (!isnan(humidityMeasurement) && !isnan(temperatureMeasurement)) {
        Serial.printf("C: %.3f\nH: %.3f\n", temperatureMeasurement, humidityMeasurement);
    }

    SensorReadings readings;
    readings.humidity = humidityMeasurement;
    readings.temperature = temperatureMeasurement;

    return readings;
}

Pump* getPump() {
    return pump;
}

RealIO* getRealIO() {
    return realIO;
}