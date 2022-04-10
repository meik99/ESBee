package log

import (
	logr "github.com/sirupsen/logrus"
)

var log *logr.Logger

func GetLogger() *logr.Logger {
	if log == nil {
		log = logr.New()
		log.SetLevel(logr.DebugLevel)
	}

	return log
}
