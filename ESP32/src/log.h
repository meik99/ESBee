//
// Created by michael on 5/22/22.
//

#ifndef ESP32_LOG_H
#define ESP32_LOG_H

#include <string>
#include <sstream>
#include <Arduino.h>

#define LOG_LEVEL_DEBUG "debug"
#define LOG_LEVEL_INFO "info"
#define LOG_LEVEL_WARN "warning"
#define LOG_LEVEL_ERR "error"

#define MAX_LOG_ENTRIES 256

struct LogEntry {
    long time;
    std::string message;
    std::string level;
};

class Log {
private:
    LogEntry entries[MAX_LOG_ENTRIES];
    int index = 0;
    int count = 0;
public:
    void log(std::string level, std::string message);
    void d(std::string message);
    void i(std::string message);
    void w(std::string message);
    void e(std::string message);
    std::string toJSONMessage();
};


#endif //ESP32_LOG_H
