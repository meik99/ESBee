//
// Created by michael on 5/7/22.
//

#ifndef ESP32_BLUETOOTH_H
#define ESP32_BLUETOOTH_H

#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <BLECharacteristic.h>

#include <string>

#define BLUETOOTH_NAME "Bee-Saver-Tron 3000"
#define BEE_SERVICE_UUID "181A"
#define CHARACTERISTIC_TEMPERATURE_UUID "2A1C"
#define CHARACTERISTIC_HUMIDITY_UUID "2A6F"
#define CHARACTERISTIC_PUMP_CONTROL_UUID "00002a9f-0000-1000-8000-00805f9b34fb"
#define PUMP_ON "ON"
#define PUMP_OFF "OFF"


class RestartAdvertisingCallback: public BLEServerCallbacks {
public:
    void onConnect(BLEServer* server);
    void onDisconnect(BLEServer* server);
};

class PumpControlCallback: public BLECharacteristicCallbacks {
public:
    void onRead(BLECharacteristic *characteristic);
    void onWrite(BLECharacteristic *characteristic);
};

void initBleServer();
void updatePumpControl();
void setTemperature(float temperature);
void setHumidity(float humidity);
std::string getPumpControl();

#endif //ESP32_BLUETOOTH_H
