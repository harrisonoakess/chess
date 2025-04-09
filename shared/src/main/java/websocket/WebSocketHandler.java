package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageExtended;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final DBAuthDAO authDAO = new DBAuthDAO();
    private final DBGameDAO gameDAO = new DBGameDAO();
    private final Gson gson = new Gson();
    private Connection connection = null;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand gameAction = gson.fromJson(message, UserGameCommand.class);
        UserMoveCommand moveAction = gson.fromJson(message, UserMoveCommand.class);
        switch (gameAction.getCommandType()) {
            case CONNECT -> connect(gameAction.getAuthToken(), gameAction.getGameID(), session);
            case MAKE_MOVE -> makeMove(moveAction.getAuthToken(), moveAction.getGameID(), moveAction.getMove(), session);
        }
}

    private void connect(String authToken, int gameID, Session session) throws IOException {
//        Connection connection = null;
        try {
            // check auth and ID
            String username = authDAO.returnUsername(authToken);
            GameData gameData = gameDAO.listGames().get(gameID);
            checkAuthAndGame(authToken, gameID, username, gameData);

            // establish connection and add to list
            connection = new Connection(username, session);
            connections.add(username, gameID, connection);

            // send message to server
            ServerMessageExtended response = new ServerMessageExtended(ServerMessage.ServerMessageType.LOAD_GAME);
            response.game = gameData.game();
            connection.send(gson.toJson(response));

            // send message back to other players "broadcast"
            ServerMessageExtended notification = new ServerMessageExtended(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.message = username + " has joined the game";
            connections.broadcast(gameID, username, gson.toJson(notification));
        } catch (DataAccessException | SQLException e) {
            ServerMessageExtended error = new ServerMessageExtended(ServerMessage.ServerMessageType.ERROR);
            error.errorMessage = "Error: " + e.getMessage();
            if (connection != null) {
                connection.send(gson.toJson(error));
            } else {
                session.getRemote().sendString(gson.toJson(error));
            }
        }
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
//        Connection connection = null;
        try {
            // check auth and IDg
            String username = authDAO.returnUsername(authToken);
            GameData gameData = gameDAO.listGames().get(gameID);
            checkAuthAndGame(authToken, gameID, username, gameData);
            // make sure user is in the game
            connection = connections.gameConnections.get(gameID).get(username);
            if (connection == null) throw new DataAccessException("User is not in the game");

            // make sure its your turn
            ChessGame currentGame = gameData.game();
            ChessGame.TeamColor playerColor = null;
            if (Objects.equals(gameData.whiteUsername(), username)) playerColor = ChessGame.TeamColor.WHITE;
            if (Objects.equals(gameData.blackUsername(), username)) playerColor = ChessGame.TeamColor.BLACK;
            if (currentGame.getTeamTurn() != playerColor) throw new DataAccessException("Its not your turn");

            // check to see if the move is valid then makes the move if it is
//            Collection<ChessMove> validMoves = currentGame.validMoves(move.getStartPosition());
            if (!currentGame.validMoves(move.getStartPosition()).contains(move)) throw new DataAccessException("You cannot move there");
            currentGame.makeMove(move);

            // updates game in SQL
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
            gameDAO.updateGame(gameData);

            // send message to server
            ServerMessageExtended response = new ServerMessageExtended(ServerMessage.ServerMessageType.LOAD_GAME);
            response.game = currentGame;
            connections.broadcast(gameID, null, gson.toJson(response));

// send message back to other players "broadcast"
            ServerMessageExtended notification = new ServerMessageExtended(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.message = username + " has moved to " + move.getEndPosition();
            connections.broadcast(gameID, username, gson.toJson(notification));

    } catch (DataAccessException | SQLException e) {
            ServerMessageExtended error = new ServerMessageExtended(ServerMessage.ServerMessageType.ERROR);
            error.errorMessage = "Error: " + e.getMessage();
            if (connection != null) {
                connection.send(gson.toJson(error));
            } else {
                session.getRemote().sendString(gson.toJson(error));
            }
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    private void leave(String authToken, int gameID, Session sesssion) throws DataAccessException, SQLException {
        try {
            // check auth and IDg
            String username = authDAO.returnUsername(authToken);
            GameData gameData = gameDAO.listGames().get(gameID);
            checkAuthAndGame(authToken, gameID, username, gameData);

            // make sure user is in the game
            connection = connections.gameConnections.get(gameID).get(username);
            if (connection == null) throw new DataAccessException("User is not in the game");

            connections.remove(username, gameID);

            ServerMessageExtended notification = new ServerMessageExtended(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.message = username + " has left the game";
            connections.broadcast(gameID, username, gson.toJson(notification));
        } catch (DataAccessException | SQLException | IOException e) {
            ServerMessageExtended error = new ServerMessageExtended(ServerMessage.ServerMessageType.ERROR);
            error.errorMessage = "Error: " + e.getMessage();
        }
    }

    private void checkAuthAndGame(String authToken, int gameID) throws DataAccessException, SQLException {
        // check auth and ID
        String username = authDAO.returnUsername(authToken);
        GameData gameData = gameDAO.listGames().get(gameID);
        if (username == null) throw new DataAccessException("Auth token does note exist");
        if (gameData == null) throw new DataAccessException("Game ID does not exist");

    }


    @OnWebSocketConnect
    public void onOpen(Session session) {
        System.out.printf("WebSocket connected: %s%n", session.getRemoteAddress());
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
        System.out.printf("WebSocket closed (%d): %s%n", statusCode, reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable cause) {
        System.err.println("WebSocket error");
    }
}
