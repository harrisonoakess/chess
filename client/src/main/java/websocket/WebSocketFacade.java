package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import org.glassfish.tyrus.core.wsadl.model.Endpoint;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    //Endpoint requires this method, but you don't have to do anything
//    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }



    public void connect(String gameID, String authToken) throws ResponseException{
        try {
            var connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, Integer.parseInt(gameID));
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
        } catch (IOException exception) {
            throw new ResponseException(500, "CONNECT Failed: " + exception.getMessage());
        }
    }
    public void makeMove(String gameID, String authToken) throws ResponseException{
        try {
            var makeMoveCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, Integer.parseInt(gameID));
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
        } catch (IOException exception) {
            throw new ResponseException(500, "MAKE_MOVE Failed: " + exception.getMessage());
        }
    }
    public void leave(String gameID, String authToken) throws ResponseException{
        try {
            var leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, Integer.parseInt(gameID));
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch (IOException exception) {
            throw new ResponseException(500, "LEAVE Failed: " + exception.getMessage());
        }
    }
    public void resign(String gameID, String authToken) throws ResponseException{
        try {
            var resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, Integer.parseInt(gameID));
            this.session.getBasicRemote().sendText(new Gson().toJson(resignCommand));
        } catch (IOException exception) {
            throw new ResponseException(500, "RESIGN Failed: " + exception.getMessage());
        }
    }
}

// Subclass for MAKE_MOVE, it says not to edit USerGameCommand
class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }
}

// Subclass for ServerMessage, it says not to edit ServerMessage

