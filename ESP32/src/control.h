//
// Created by michael on 5/7/22.
//

#ifndef ESP32_CONTROL_H
#define ESP32_CONTROL_H


void testPump(RealIO *realIO);
void testFan(RealIO *realIO);
void readHumidityAndTemperature();
void setupControl();
void runControl();

#endif //ESP32_CONTROL_H
