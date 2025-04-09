package websocket;

import websocket.messages.ServerMessage;


public interface NotificationHandler {
    void notify(ServerMessageExtended serverMessage);

    void notify(websocket.messages.ServerMessageExtended serverMessageExtended);
}
