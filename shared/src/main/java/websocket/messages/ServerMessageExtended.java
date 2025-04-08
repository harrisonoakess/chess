package websocket.messages;

public class ServerMessageExtended extends ServerMessage {
    public String message;
    public String errorMessage;
    public chess.ChessGame game;

    public ServerMessageExtended(ServerMessageType type) {
        super(type);
    }
}