//
// Created by michael on 5/7/22.
//

#include "bluetooth.h"

BLECharacteristic *characteristicTemperature;
BLECharacteristic *characteristicHumidity;
BLECharacteristic *characteristicPumpControl;
std::string lastControlValue = PUMP_OFF;

void RestartAdvertisingCallback::onConnect(BLEServer *server) {
    BLEServerCallbacks::onConnect(server);

    Serial.println("device connected");
}

void RestartAdvertisingCallback::onDisconnect(BLEServer *server) {
    BLEServerCallbacks::onDisconnect(server);

    Serial.println("device disconnected, restarting advertising");
    delay(500);
    BLEDevice::startAdvertising();
    server->startAdvertising();
}

void PumpControlCallback::onRead(BLECharacteristic *characteristic) {
    BLECharacteristicCallbacks::onRead(characteristic);
}

void PumpControlCallback::onWrite(BLECharacteristic *characteristic) {
    BLECharacteristicCallbacks::onWrite(characteristic);

    Serial.println("characteristic changed");
    if(characteristic->getUUID().toString() == CHARACTERISTIC_PUMP_CONTROL_UUID) {
        Serial.println("pump control changed");
        updatePumpControl();
    }
}

void initBleServer() {
    BLEDevice::init(BLUETOOTH_NAME);
    BLEServer *server = BLEDevice::createServer();
    BLEAdvertising *advertising = BLEDevice::getAdvertising();
    BLEService *sensorService = server->createService(BEE_SERVICE_UUID);

    characteristicTemperature = sensorService->createCharacteristic(
            CHARACTERISTIC_TEMPERATURE_UUID,
            BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_NOTIFY
    );

    characteristicHumidity = sensorService->createCharacteristic(
            CHARACTERISTIC_HUMIDITY_UUID,
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
    characteristicPumpControl->setCallbacks(new PumpControlCallback());
    sensorService->start();

    advertising->addServiceUUID(BEE_SERVICE_UUID);
    advertising->setScanResponse(true);

    // functions that help with iPhone connections issue
    advertising->setMinPreferred(0x06);
    advertising->setMinPreferred(0x12);

    BLEDevice::startAdvertising();

    characteristicPumpControl->setValue(lastControlValue);
    characteristicPumpControl->notify();
}

void setTemperature(float temperature) {
    characteristicTemperature->setValue(std::to_string(temperature));
    characteristicTemperature->notify();
}

void setHumidity(float humidity) {
    characteristicHumidity->setValue(std::to_string(humidity));
    characteristicHumidity->notify();
}

void updatePumpControl() {
    std::string pumpControl = characteristicPumpControl->getValue();

    if (pumpControl.compare(lastControlValue) != 0) {
        lastControlValue = pumpControl;

        characteristicPumpControl->setValue(lastControlValue);
        characteristicPumpControl->notify();

        Serial.print("pump control: ");
        Serial.println(lastControlValue.c_str());
    }
}

std::string getPumpControl() {
    return lastControlValue;
}