//#include "control.h"

#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

#include <string>

#define BEE_SERVICE_UUID "181A"
#define CHARACTERISTIC_TEMPERATURE_UUID "2A1C"
#define CHARACTERISTIC_PUMP_CONTROL_UUID "2A9F"

BLECharacteristic *characteristicTemperature;
BLECharacteristic *characteristicPumpControl;

class RestartAdvertisingCallback: public BLEServerCallbacks {
    void onConnect(BLEServer* server) {
        Serial.println("device connected");
    };

    void onDisconnect(BLEServer* server) {
        Serial.println("device disconnected, restarting advertising");
        delay(500);
        BLEDevice::startAdvertising();
        server->startAdvertising();
    }
};

std::string lastControlValue = "OFF";

void setup() {
    Serial.begin(115200);

    while(!Serial) {
        delay(100);
    }

    BLEDevice::init("Bee-saver-tron 3000");
    BLEServer *server = BLEDevice::createServer();
    BLEAdvertising *advertising = BLEDevice::getAdvertising();
    BLEService *sensorService = server->createService(BEE_SERVICE_UUID);

    characteristicTemperature = sensorService->createCharacteristic(
            CHARACTERISTIC_TEMPERATURE_UUID,
            BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_NOTIFY
    );

    characteristicPumpControl = sensorService->createCharacteristic(
            CHARACTERISTIC_PUMP_CONTROL_UUID,
            BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_NOTIFY |
            BLECharacteristic::PROPERTY_WRITE
    );

    server->setCallbacks(new RestartAdvertisingCallback());
    sensorService->start();

    advertising->addServiceUUID(BEE_SERVICE_UUID);
    advertising->setScanResponse(true);

    advertising->setMinPreferred(0x06);  // functions that help with iPhone connections issue
    advertising->setMinPreferred(0x12);

    BLEDevice::startAdvertising();

    characteristicPumpControl->setValue(lastControlValue);
    characteristicPumpControl->notify();
}

void loop() {
    int temp = random(-20, 40);
    characteristicTemperature->setValue(std::to_string(temp));
    characteristicTemperature->notify();
    Serial.println(std::to_string(temp).c_str());

    std::string pumpControl = characteristicPumpControl->getValue();

    if (pumpControl.compare(lastControlValue) != 0) {
        Serial.println(pumpControl.c_str());
        lastControlValue = pumpControl;
    }

    characteristicPumpControl->setValue(lastControlValue);
    characteristicPumpControl->notify();

    delay(1000);
}