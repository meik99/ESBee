//#include "control.h"

#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

#include <string>

#define SENSOR_SERVICE_UUID "181A"
#define CHARACTERISTIC_TEMPERATURE_UUID "2A1C"

BLECharacteristic *characteristicTemperature;

void setup() {
    Serial.begin(115200);

    while(!Serial) {
        delay(100);
    }

    // Wait five seconds so monitor has time to connect
    delay(2000);

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
    double temp = 10;
    characteristicTemperature->setValue(temp);
    characteristicTemperature->notify();

    delay(1000);
}