//
// Created by michael on 4/6/22.
//

#include <realio.h>

void RealIO::on(int pin) {
    this->pinMap[pin] = true;
    digitalWrite(pin, HIGH);
}

void RealIO::off(int pin) {
    this->pinMap[pin] = false;
    digitalWrite(pin, LOW);
}

void RealIO::toggle(int pin) {
    if (this->pinMap.find(pin) == this->pinMap.end()) {
        this->pinMap[pin] = false;
    }

    if (this->pinMap[pin]) {
        this->off(pin);
    } else {
        this->on(pin);
    }
}