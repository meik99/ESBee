//
// Created by michael on 4/6/22.
//

#include <map>
#include <pinio.h>
#include <Arduino.h>

#ifndef ESP32_GPIO_H
#define ESP32_GPIO_H


class RealIO: public PinIO{
private:
    std::map<int, bool> pinMap;
public:
    void on(int pin);
    void off(int pin);
    void toggle(int pin);
};


#endif //ESP32_GPIO_H
