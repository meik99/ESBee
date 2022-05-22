//
// Created by michael on 5/7/22.
//

#ifndef ESP32_CONTROL_H
#define ESP32_CONTROL_H

#include <Arduino.h>
#include <pump.h>
#include <humidity.h>

#include "realio/realio.h"
#include "dht_repository/dht_repository.h"

#define PUMP_RELAY 27
#define HUMIDITY_SENSOR 25
#define FAN 26

struct SensorReadings {
    float humidity;
    float temperature;
};

SensorReadings readHumidityAndTemperature();
void setupControl();
Pump* getPump();
RealIO* getRealIO();

#endif //ESP32_CONTROL_H
