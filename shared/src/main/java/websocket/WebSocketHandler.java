package websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageExtended;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;


@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(action.getAuthToken(), action.getGameID(), session);
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        Connection connection = null;
        try {
            DBAuthDAO authDAO = new DBAuthDAO();
            DBGameDAO gameDAO = new DBGameDAO();
            String username = authDAO.returnUsername(authToken);
            if (username == null) throw new DataAccessException("Auth token does note exist");
            GameData gameData = gameDAO.listGames().get(gameID);
            if (gameData == null) throw new DataAccessException("Game ID does not exist");

            connection = new Connection(username, session);
            connections.add(username, gameID, connection);

            ServerMessageExtended response = new ServerMessageExtended(ServerMessage.ServerMessageType.LOAD_GAME);
            response.game = gameData.game();
            connection.send(new Gson().toJson(response));

            ServerMessageExtended notification = new ServerMessageExtended(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.message = username + " has joined the game";
            connections.broadcast(gameID, username, new Gson().toJson(notification));
        } catch (DataAccessException | SQLException e) {
            ServerMessageExtended error = new ServerMessageExtended(ServerMessage.ServerMessageType.ERROR);
            error.errorMessage = "Error: " + e.getMessage();
            if (connection != null) {
                connection.send(new Gson().toJson(error));
            } else {
                session.getRemote().sendString(new Gson().toJson(error));
            }
        }
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        System.out.println("WebSocket connected: " + session.getRemoteAddress());
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        for (var gameEntry : connections.gameConnections.entrySet()) {
            int gameID = gameEntry.getKey();
            Map<String, Connection> users = gameEntry.getValue();
            for (var userEntry : users.entrySet()) {
                if (userEntry.getValue().session.equals(session)) {
                    connections.remove(userEntry.getKey(), gameID);
                    break;
                }
            }
        }
        System.out.println("WebSocket closed: " + statusCode + ", " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable cause) {
        System.err.println("WebSocket error");
    }
}
