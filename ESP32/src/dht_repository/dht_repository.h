#include <humidity.h>
#include "DHT.h"

#ifndef ESP32_DHT_REPOSITORY_H
#define ESP32_DHT_REPOSITORY_H

class DHTRepository: public HumidityRepository {
private: 
    DHT sensor;    
public:
    DHTRepository(int pin);
    float getTemperature();
    float getHumidity();
};

#endif //ESP32_DHT_REPOSITORY_H