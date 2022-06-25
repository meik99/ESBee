//
// Created by michael on 5/22/22.
//

#include "log.h"

void Log::log(std::string level, std::string message) {
    LogEntry logEntry;
    logEntry.time = millis();
    logEntry.level = level;
    logEntry.message = message;

    this->entries[this->index] = logEntry;
    this->index++;

    // Increase count to know until which log entry to iterate to for parsing in Log::toJSONMessage()
    if (this->count < MAX_LOG_ENTRIES) {
        this->count++;
    }

    // Due to the limited ram size of the ESP32
    // Rollover log lines after MAX_LOG_ENTRIES
    if (this->index >= MAX_LOG_ENTRIES) {
        this->index = 0;
    }
}

void Log::d(std::string message) {
    this->log(LOG_LEVEL_DEBUG, message);
}

void Log::i(std::string message) {
    this->log(LOG_LEVEL_INFO, message);
}

void Log::w(std::string message) {
    this->log(LOG_LEVEL_WARN, message);
}

void Log::e(std::string message) {
    this->log(LOG_LEVEL_ERR, message);
}

std::string Log::toJSONMessage() {
    std::stringstream logResult;

    logResult << "[";

    for (int i = 0; i < this->count; i++) {
        logResult << "{";
        logResult << "\"time\":" << this->entries[i].time << ",";
        logResult << "\"level\":\"" << this->entries[i].level << "\",";
        logResult << "\"message\":\"" << this->entries[i].message << "\"";
        logResult << "}";

        if (i < this->count - 1) {
            logResult << ",";
        }
    }

    logResult << "]";

    return logResult.str();
}