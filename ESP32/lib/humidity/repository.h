#ifndef ESP32_HUMIDITY_REPOSITORY_H
#define ESP32_HUMIDITY_REPOSITORY_H

class HumidityRepository {
public:
    virtual float getTemperature(){return 0;};
    virtual float getHumidity(){return 0;};
};

#endif //ESP32_HUMIDITY_REPOSITORY_H
