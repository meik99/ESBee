//
// Created by michael on 6/10/22.
//

#include "gsm.h"

void GSM::init() {
    Serial.println("connecting modem");

    this->serialAT.begin(9600,SERIAL_8N1,16,17,false);
    this->modem.setBaud(9600);
    if (!this->modem.restart()) {
        Serial.println("modem failed to start");
    }
    this->modem.simUnlock();

    int retries = 0;
    while(!modem.waitForNetwork(600000L, true)) {
        delay(10000);
        retries++;

        if (retries > 10) {
            Serial.println("timeout waiting for network");
            break;
        } else {
            Serial.print("waiting for network ");
            Serial.print(retries);
            Serial.println(" / 10");
        }
    }

    retries = 0;
    while (!this->modem.gprsConnect()) {
        delay(10000);
        retries++;

        if (retries > 30) {
            Serial.println("timeout waiting for gprs connect");
            break;
        } else {
            Serial.print("waiting for gprs connect ");
            Serial.print(retries);
            Serial.println(" / 30");
        }
    }
}