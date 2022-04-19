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
    /**
     * Starts the pump by calling PinIO::on of the provided PinIO interface.
     * @param startTime A long indicating the time passed since the program started
     */
    void start(long startTime);
    /**
     * Stops the pump by calling PinIO::off of the provided PinIO interface.
     */
    void stop();
    /**
     * Returns how many milliliters were approximately pumped between the @startTime provided by @Pump::start
     * and the @currentTime
     * @param currentTime A long indicating the time passed since the program started
     * @return A float representing the milliliters that were approximately pumped
     */
    float pumpedMilliliter(long currentTime);
    /**
     * Returns how many grams were approximately pumped between the @startTime provided by @Pump::start
     * and the @currentTime. It does so by multiplying @Pump::pumpedMilliliter by the density of formic acid
     * @param currentTime A long indicating the time passed since the program started
     * @return  A float representing the grams that were approximately pumped
     */
    float pumpedGrams(long currentTime);
    /**
     * Returns whether or not the pump is currently pumping
     * @return A boolean
     */
    bool isPumping();
};


#endif //ESP32_PUMP_H
