#include "dht_repository.h"

#include <DHT.h>

#define DHTTYPE DHT11

DHTRepository::DHTRepository(int pin) : sensor(DHT(pin, DHTTYPE)) {
    this->sensor.begin();
}

float DHTRepository::getTemperature() {
    return this->sensor.readTemperature();
}

float DHTRepository::getHumidity() {
    return this->sensor.readHumidity();
}