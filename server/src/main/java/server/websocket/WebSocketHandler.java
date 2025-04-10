package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
//    private Connection connection = null;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, SQLException, DataAccessException {
        UserGameCommand gameAction = gson.fromJson(message, UserGameCommand.class);
        UserMoveCommand moveAction = gson.fromJson(message, UserMoveCommand.class);
        switch (gameAction.getCommandType()) {
            case CONNECT -> connect(gameAction.getAuthToken(), gameAction.getGameID(), session);
            case MAKE_MOVE -> makeMove(moveAction.getAuthToken(), moveAction.getGameID(), moveAction.getMove(), session);
            case LEAVE -> leave(gameAction.getAuthToken(), gameAction.getGameID(), session);
            case RESIGN -> resign(gameAction.getAuthToken(), gameAction.getGameID(), session);
        }
}

    private void connect(String authToken, int gameID, Session session) throws IOException {
        Connection connection = null;
        try {
            // check auth and ID
            String username = authDAO.returnUsername(authToken);
            GameData gameData = gameDAO.listGames().get(gameID);
            checkAuthAndGame(username, gameData);

            // establish connection and add to list
            connection = new Connection(username, session);
            connections.add(username, gameID, connection);

            // send message to server
            ServerMessageExtended response = new ServerMessageExtended(ServerMessage.ServerMessageType.LOAD_GAME);
            response.game = gameData.game();
            connection.send(gson.toJson(response));

            // send message back to other players "broadcast"
            ServerMessageExtended notification = new ServerMessageExtended(ServerMessage.ServerMessageType.NOTIFICATION);
            String role;
            if (Objects.equals(gameData.whiteUsername(), username)) {
                role = "WHITE";
            } else if (Objects.equals(gameData.blackUsername(), username)) {
                role = "BLACK";
            } else {
                role = "an observer";
            }
            notification.message = username + " has joined the game as " + role;
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
        Connection connection = null;
        try {
            // check auth and IDg
            String username = authDAO.returnUsername(authToken);
            GameData gameData = gameDAO.listGames().get(gameID);
            checkAuthAndGame(username, gameData);
            // make sure user is in the game
            connection = connections.gameConnections.get(gameID).get(username);
            if (connection == null) throw new DataAccessException("User is not in the game");

            // make sure its your turn
            ChessGame currentGame = gameData.game();
            ChessGame.TeamColor playerColor = null;
            if (Objects.equals(gameData.whiteUsername(), username)) playerColor = ChessGame.TeamColor.WHITE;
            if (Objects.equals(gameData.blackUsername(), username)) playerColor = ChessGame.TeamColor.BLACK;
            if (currentGame.getTeamTurn() != playerColor) throw new DataAccessException("Its not your turn");
            if (playerColor == null) throw new DataAccessException("Observers cannot make moves");
            // check if game is still going
            if (currentGame.isGameOver()) throw new DataAccessException("Game is already over");

            // check to see if the move is valid then makes the move if it is
            if (!currentGame.validMoves(move.getStartPosition()).contains(move)) throw new DataAccessException("You cannot move there");
            currentGame.makeMove(move);

            // check if in check or checkmate
            ChessGame.TeamColor opponentColor;
            String opponentName;
            if (playerColor == ChessGame.TeamColor.WHITE) {
                opponentColor = ChessGame.TeamColor.BLACK;
                opponentName = gameData.blackUsername();
            } else {
                opponentColor = ChessGame.TeamColor.WHITE;
                opponentName = gameData.whiteUsername();
            }
            if (currentGame.isInCheckmate(opponentColor)) {
                currentGame.gameFinished(true);
                ServerMessageExtended checkmateNotification = new ServerMessageExtended(ServerMessageExtended.ServerMessageType.NOTIFICATION);
                checkmateNotification.message = opponentName + " is in checkmate";
                connections.broadcast(gameID, null, gson.toJson(checkmateNotification));
            } else if (currentGame.isInCheck(opponentColor)){
                ServerMessageExtended checkNotification = new ServerMessageExtended(ServerMessageExtended.ServerMessageType.NOTIFICATION);
                checkNotification.message = opponentName + " is in check";
                connections.broadcast(gameID, null, gson.toJson(checkNotification));
            }

            // updates game in SQL
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), currentGame);
            gameDAO.updateGame(updatedGame);

            // send message to server
            ServerMessageExtended response = new ServerMessageExtended(ServerMessage.ServerMessageType.LOAD_GAME);
            response.game = currentGame;
            connections.broadcast(gameID, null, gson.toJson(response));

// send message back to other players "broadcast"
            ServerMessageExtended notification = new ServerMessageExtended(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.message = username + " has moved from " + positionToString(move.getStartPosition()) + " to " + positionToString(move.getEndPosition());
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

    private void leave(String authToken, int gameID, Session sesssion) throws DataAccessException, SQLException, IOException {
        Connection connection = null;
        try {
            // check auth and IDg
            String username = authDAO.returnUsername(authToken);
            GameData gameData = gameDAO.listGames().get(gameID);
            checkAuthAndGame(username, gameData);

            // make sure user is in the game
            connection = connections.gameConnections.get(gameID).get(username);
            if (connection == null) throw new DataAccessException("User is not in the game");

            //remove player from game (in database)
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();
            if (Objects.equals(gameData.whiteUsername(), username)) whiteUsername = null;
            if (Objects.equals(gameData.blackUsername(), username)) blackUsername = null;
            GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameData.gameName(), gameData.game());
            gameDAO.updateGame(updatedGame);

            // remove player from connection
            connections.remove(username, gameID);

            ServerMessageExtended notification = new ServerMessageExtended(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.message = username + " has left the game";
            connections.broadcast(gameID, username, gson.toJson(notification));
        } catch (DataAccessException | SQLException | IOException e) {
            ServerMessageExtended error = new ServerMessageExtended(ServerMessage.ServerMessageType.ERROR);
            error.errorMessage = "Error: " + e.getMessage();
            if (connection != null) {
                connection.send(gson.toJson(error));
            } else {
                sesssion.getRemote().sendString(gson.toJson(error));
            }
        }
    }

    private void resign(String authToken, int gameID, Session session) throws IOException {
        Connection connection = null;
        try {
            // check auth and IDg
            String username = authDAO.returnUsername(authToken);
            GameData gameData = gameDAO.listGames().get(gameID);
            checkAuthAndGame(username, gameData);

            // make sure user is in the game
            connection = connections.gameConnections.get(gameID).get(username);
            if (connection == null) throw new DataAccessException("User is not in the game");

            // check for observer resignation
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();
            if (!username.equals(whiteUsername) && !username.equals(blackUsername)) throw new DataAccessException("You must be playing to resign");

            // check if game is still going
            ChessGame currentGame = gameData.game();
            if (currentGame.isGameOver()) throw new DataAccessException("Game is already over");

            //end the game
            currentGame.gameFinished(true);
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), currentGame);
            gameDAO.updateGame(updatedGame);

            // send message to everyone
            ServerMessageExtended notification = new ServerMessageExtended(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.message = username + " has resigned";
            connections.broadcast(gameID, null, gson.toJson(notification));
        } catch (SQLException | DataAccessException | IOException e) {
            ServerMessageExtended error = new ServerMessageExtended(ServerMessage.ServerMessageType.ERROR);
            error.errorMessage = "Error: " + e.getMessage();
            if (connection != null) {
                connection.send(gson.toJson(error));
            } else {
                session.getRemote().sendString(gson.toJson(error));
            }
        }
    }

    private void checkAuthAndGame(String username, GameData gameData) throws DataAccessException, SQLException {
        // check auth and ID
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

    private String positionToString(ChessPosition position) {
        char column = (char) ('a' + position.getColumn() - 1); // Convert 1-based column to letter (1 = 'a', 2 = 'b', etc.)
        int row = position.getRow(); // Row is already 1-based
        return "" + column + row; // e.g., "e2"
    }
}
