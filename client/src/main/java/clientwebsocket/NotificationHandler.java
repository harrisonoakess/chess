package clientwebsocket;

import websocket.messages.ServerMessageExtended;


public interface NotificationHandler {
    void notify(ServerMessageExtended serverMessage);
}
