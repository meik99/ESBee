//
// Created by michael on 4/6/22.
//

#include <pinio.h>

#ifndef ESP32_PUMP_H
#define ESP32_PUMP_H

// 1/2 ml/s = 1/2 * 10^-3 ml/ms
#define PUMP_SPEED 0.0005f

// formic acid has a density of 1.22 g/ml
#define FORMIC_ACID_DENSITY 1.22f

class Pump {
private:
    PinIO *pinIo;
    bool pumping;
    int pin = -1;
    long startTime = 0;

public:
    Pump(PinIO *pinIo, int pin);
    // 1/2 ml / s
    void start(long startTime);
    void stop();
    float pumpedMilliliter(long currentTime);
    float pumpedGrams(long currentTime);
    bool isPumping();    
};


#endif //ESP32_PUMP_H
