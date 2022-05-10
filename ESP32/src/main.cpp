//#include "control.h"

#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

#include <string>

#define SENSOR_SERVICE_UUID "181A"
// #define SENSOR_SERVICE_UUID "83b5cd4d-69ac-4d05-951c-009b0f0db3d3"
#define CHARACTERISTIC_TEMPERATURE_UUID "2A1C"
//#define CHARACTERISTIC_TEMPERATURE_UUID "28ecc560-82ed-4438-98ac-9870c977cb44"

BLECharacteristic *characteristicTemperature;

void setup() {
    Serial.begin(115200);

    while(!Serial) {
        delay(100);
    }

    BLEDevice::init("Bee-saver-tron 3000");
    BLEServer *server = BLEDevice::createServer();
    BLEAdvertising *advertising = BLEDevice::getAdvertising();
    BLEService *service = server->createService(SENSOR_SERVICE_UUID);

    characteristicTemperature = service->createCharacteristic(
            CHARACTERISTIC_TEMPERATURE_UUID,
            BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_NOTIFY
    );

    service->start();

    advertising->addServiceUUID(SENSOR_SERVICE_UUID);
    advertising->setScanResponse(true);
    BLEDevice::startAdvertising();
}

void loop() {
    int temp = random(-20, 40);
    characteristicTemperature->setValue(std::to_string(temp));
    characteristicTemperature->notify();
    Serial.printf("%d\n", temp);
    delay(1000);
}