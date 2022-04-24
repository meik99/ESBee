#include "pump.h"

Pump::Pump(PinIO *pinIo, int pin) {
    this->pinIo = pinIo;
    this->pin = pin;
}

void Pump::start(long startTime) {
    this->pinIo->on(this->pin);
    this->startTime = startTime;
    this->pumping = true;
}

void Pump::stop() {
    this->pinIo->off(this->pin);
    this->pumping = false;
}

float Pump::pumpedMilliliter(long currentTime) {
    return (currentTime - this->startTime) * PUMP_SPEED;
}

float Pump::pumpedGrams(long currentTime) {
    return this->pumpedMilliliter(currentTime) * FORMIC_ACID_DENSITY;
}

bool Pump::isPumping() {
    return this->pumping;
}