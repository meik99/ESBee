//
// Created by michael on 4/6/22.
//

#include <pinio.h>

#ifndef ESP32_PUMP_H
#define ESP32_PUMP_H


class Pump {
private:
    PinIO *pinIo;

public:
    Pump(PinIO *pinIo) {
        this->pinIo = pinIo;
    }

    int pumpMilliliters(int deltaTime, float milliliter) {
        return 0;
    }
};


#endif //ESP32_PUMP_H
