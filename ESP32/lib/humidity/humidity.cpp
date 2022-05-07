#include "humidity.h"
#include "repository.h"
#include <math.h>

float sanitizeMeasurement(float measurement);

Humidity::Humidity(HumidityRepository *repository) {
    this->repository = repository;
}

void Humidity::measure() {
    this->temperature = this->repository->getTemperature();
    this->humidity = this->repository->getHumidity();    
}

float Humidity::getTemperature() {
    return this->temperature;
}

float Humidity::getHumidity() {
    return this->humidity;
}