#ifndef ESP32_GSM_H
#define ESP32_GSM_H

#define TINY_GSM_MODEM_SIM800

#include <HardwareSerial.h>
#include <TinyGsmClient.h>

class GSM {
private:
    HardwareSerial serialAT;;
    TinyGsm modem;
    TinyGsmClient client;
public:
    GSM() : serialAT(2) /*UART 2*/, modem(serialAT), client(modem) {};
    void init();
};


#endif // ESP32_GSM_H
