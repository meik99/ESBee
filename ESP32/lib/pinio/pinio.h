//
// Created by michael on 4/6/22.
//

#ifndef ESP32_PINIO_H
#define ESP32_PINIO_H


class PinIO {
public:
    virtual void on(int pin) {};
    virtual void off(int pin) {};
    virtual void toggle(int pin) {};
};


#endif //ESP32_PINIO_H
