package control

import (
	"github.com/gorilla/websocket"
	"github.com/meik99/ESBee/internal/log"
	"net/http"
	"time"
)

func Handle(writer http.ResponseWriter, request *http.Request) {
	upgrader := &websocket.Upgrader{
		CheckOrigin: func(request *http.Request) bool {
			return true
		},
	}
	connection, err := upgrader.Upgrade(writer, request, nil)

	if err != nil {
		log.GetLogger().Error("error upgrading connection", err)
		return
	}

	go handleWebsocket(connection, writer, request)
}

func handleWebsocket(connection *websocket.Conn, writer http.ResponseWriter, request *http.Request) {
	defer func() {
		err := connection.Close()

		if err != nil {
			log.GetLogger().Error("error closing connection", err)
		}

		log.GetLogger().Info("connection closed")
	}()
	err := connection.SetReadDeadline(time.Time{})

	if err != nil {
		log.GetLogger().Error("error setting read deadline", err)
		return
	}

	err = connection.WriteMessage(websocket.TextMessage, []byte("ping"))

	if err != nil {
		log.GetLogger().Error("error when sending ping message ", err)
		return
	}

	messageType, message, err := connection.ReadMessage()

	if err != nil {
		log.GetLogger().Error("error when receiving pong message ", err)
		return
	}

	if messageType != websocket.TextMessage {
		log.GetLogger().Warning("did not receive correct message type after ping",
			" received '", messageType, "' expected '", websocket.TextMessage, "'")
		return
	}

	if string(message) != "pong" {
		log.GetLogger().Warning("received wrong pong message",
			" expected 'pong' received '", string(message), "'")
		return
	}
}
