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
    /**
     * Sets the state of @pin to HIGH.
     * The @pin has to be configured for output before calling this method.
     * @param pin The GPIO pin to set the state of.
     */
    void on(int pin);
    /**
     * Sets the state of @pin to LOW.
     * The @pin has to be configured for output before calling this method.
     * @param pin The GPIO pin to set the state of.
     */
    void off(int pin);
    /**
     * Toggles the state of @pin.
     * If @pin is LOW it is set to HIGH.
     * If @pin is HIGH it is set to LOW.
     * @param pin The GPIO pin to toggle the state of.
     */
    void toggle(int pin);
};


#endif //ESP32_GPIO_H
