package main

import (
	"github.com/meik99/ESBee/internal/control"
	"github.com/meik99/ESBee/internal/log"
	"net/http"
)

func main() {
	log.GetLogger().Info("Starting server")

	http.HandleFunc("/api/v1/control", control.Handle)

	log.GetLogger().Errorln(http.ListenAndServe("0.0.0.0:8090", nil))
}
