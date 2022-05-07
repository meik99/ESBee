#include "repository.h"

#ifndef ESP32_HUMIDITY_H
#define ESP32_HUMIDITY_H

// Facade
class Humidity {
private: 
    HumidityRepository *repository;
    float temperature = -1;
    float humidity = -1;

public:
    Humidity(HumidityRepository *repository);
    void measure();
    float getTemperature();
    float getHumidity();
};

#endif //ESP32_HUMIDITY_H