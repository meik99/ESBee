//
// Created by michael on 4/6/22.
//

#include <pump.h>
#include <pinio.h>
#include <unity.h>
#include <iostream>
#include <map>

#ifndef ESP32_TEST_PUMP
#define ESP32_TEST_PUMP

class MockPinIO : public PinIO{
public:
    std::map<int, bool> map;
    MockPinIO() {}

    void on(int pin) {
        this->map[pin] = true;
        std::cout << "on: " << pin << std::endl;
    }
    void off(int pin) {
        this->map[pin] = false;
        std::cout << "off: " << pin << std::endl;
    }
    void toggle(int pin) {
        if (this->map.find(pin) == this->map.end()) {
            this->map[pin] = false;
        }

        if (this->map[pin]) {
            this->off(pin);
        } else {
            this->on(pin);
        }
    }
};

void test_pump(void) {
    int testPumpPin = 5;
    float startTime = 0;
    float currentTime = 0;
    float milliliters = 10;

    MockPinIO *mockPinIo = new MockPinIO();
    Pump *pump = new Pump(mockPinIo, testPumpPin);

    TEST_ASSERT(!mockPinIo->map[testPumpPin])

    pump->start(0);

    TEST_ASSERT(mockPinIo->map[testPumpPin])

    // Speed is 1/2 ml / s so double the time
    currentTime = 2 * milliliters * 1000;
    float pumpedMilliliter = pump->pumpedMilliliter(currentTime);
    float pumpedGrams = pump->pumpedGrams(currentTime);

    TEST_ASSERT(pumpedMilliliter >= milliliters && pumpedMilliliter < milliliters + 0.1f);
    TEST_ASSERT(pumpedGrams >= pumpedMilliliter * FORMIC_ACID_DENSITY && pumpedGrams <= pumpedMilliliter * FORMIC_ACID_DENSITY + 0.1f);
    TEST_ASSERT(pump->isPumping());

    pump->stop();
    TEST_ASSERT(!mockPinIo->map[testPumpPin])
    TEST_ASSERT(!pump->isPumping());
}

#endif //ESP32_TEST_PUMP