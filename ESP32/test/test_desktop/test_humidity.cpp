
#include <humidity.h>
#include <unity.h>
#include <iostream>
#include <math.h>

#ifndef ESP32_TEST_HUMIDITY
#define ESP32_TEST_HUMIDITY

bool areSame(float a, float b, float epsilon);

class MockHumidityRepository : public HumidityRepository {
public:
    float temperature = -1;
    float humidity = -1;

    float getTemperature() {
        return this->temperature;
    }

    float getHumidity() {
        return this->humidity;
    }
};

void test_humidity(void) {
    MockHumidityRepository *repository = new MockHumidityRepository();
    Humidity *humidity = new Humidity(repository);
    
    repository->temperature = 20.5;
    repository->humidity = 10.9;

    humidity->measure();

    TEST_ASSERT(areSame(humidity->getTemperature(), 20.5, 0.01));
    TEST_ASSERT(areSame(humidity->getHumidity(), 10.9, 0.01));

    repository->temperature = NAN;

    TEST_ASSERT(areSame(humidity->getTemperature(), 20.5, 0.01));
    TEST_ASSERT(areSame(humidity->getHumidity(), 10.9, 0.01));

    humidity->measure();

    TEST_ASSERT(isnan(humidity->getTemperature()));
    TEST_ASSERT(areSame(humidity->getHumidity(), 10.9, 0.01));

    repository->humidity = NAN;

    TEST_ASSERT(isnan(humidity->getTemperature()));
    TEST_ASSERT(areSame(humidity->getHumidity(), 10.9, 0.01));

    humidity->measure();

    TEST_ASSERT(isnan(humidity->getTemperature()));
    TEST_ASSERT(isnan(humidity->getHumidity()));
}

bool areSame(float a, float b, float epsilon) {
    return fabs(a - b) < epsilon;
}

#endif //ESP32_TEST_HUMIDITY