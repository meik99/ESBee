#include <Arduino.h>
#include <pump.h>
#include <humidity.h>

#include "realio/realio.h"
#include "dht_repository/dht_repository.h"

#define PUMP_RELAY 27
#define HUMIDITY_SENSOR 25

RealIO *realIO = new RealIO();
Humidity *humidity;

void testPump(RealIO *realIO);
void readHumidityAndTemperature();

void setup() {
    Serial.begin(115200);

    while(!Serial) {
        delay(100);
    }

    // Wait five seconds so monitor has time to connect
    delay(5000);

    Serial.println("serial ready");
    Serial.printf("setting pin %d to output\n", PUMP_RELAY);

    pinMode(PUMP_RELAY, OUTPUT);
    
    Serial.printf("initializing humidty sensor on pin %d\n", HUMIDITY_SENSOR);
    humidity = new Humidity(new DHTRepository(HUMIDITY_SENSOR));

    testPump(realIO);

    Serial.println("shutting down");    
    Serial.printf("shutting down pump on %d\n", PUMP_RELAY);
    digitalWrite(PUMP_RELAY, LOW);
}

void testPump(RealIO *realIO) {
    Serial.printf("initializing pump on pin %d\n", PUMP_RELAY);

    Pump *pump = new Pump(realIO, PUMP_RELAY);

    Serial.println("testing pump");

    long time = millis();
    pump->start(time);
    Serial.printf("started pump at %ld\n", time);

    do {
        delay(1000);
        time = millis();
        Serial.printf("pumped %.3f milliliter or %.3f grams in %.3f seconds \n", pump->pumpedMilliliter(time), pump->pumpedGrams(time), time / 1000.0f);
    } while(pump->pumpedMilliliter(time) < 10);

    Serial.printf("stopping pump at %ld\n", time);
    pump->stop();
}

void loop() {
    readHumidityAndTemperature();
    delay(5000);
}

void readHumidityAndTemperature() {
    humidity->measure();
    float humidityMeasurement = humidity->getHumidity();
    float temperatureMeasurement = humidity->getTemperature();

    if (isnan(humidityMeasurement)) {
        Serial.println("failed to read humidity");
    }
    if (isnan(temperatureMeasurement)) {
        Serial.println("failed to read temperature");
    }
    if (!isnan(humidityMeasurement) && !isnan(temperatureMeasurement)) {
        Serial.printf("\n\nC: %.3f\nH: %.3f\n\n", temperatureMeasurement, humidityMeasurement);
    }
}