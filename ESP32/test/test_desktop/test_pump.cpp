//
// Created by michael on 4/6/22.
//

#include <pump.h>
#include <pinio.h>
#include <unity.h>
#include <iostream>
#include <map>

class MockPinIO : public PinIO{
public:
    std::map<int, bool> map;
    MockPinIO() {}

    void on(int pin) {
        std::cout << "on: " << pin << std::endl;
    }
    void off(int pin) {}
    void toggle(int pin) {}
};

void test_pump(void) {
    PinIO *mockPinIo = new MockPinIO();
    Pump pump = Pump(new MockPinIO());
    mockPinIo->on(1);
}

int main(int argc, char **argv) {
    UNITY_BEGIN();
    RUN_TEST(test_pump);
    UNITY_END();
    return 0;
}