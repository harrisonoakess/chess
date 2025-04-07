package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.server.Response;
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
    public void makeMove(String gameID, String AuthToken) throws ResponseException{

    }
    public void leave(String gameID, String AuthToken) throws ResponseException{

    }
    public void resign(String gameID, String AuthToken) throws ResponseException{

    }
}
