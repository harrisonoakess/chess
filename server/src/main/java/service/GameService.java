package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.datastorage.DBGameDAO;
import model.CreateGameResult;
import model.GameData;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class GameService {
    private final AuthDAO authDAO;
    private final DBGameDAO gameDAO;

    public GameService(DBGameDAO gameDAO, AuthDAO authDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(String gameName, String authToken) throws DataAccessException, SQLException {
        if (!authDAO.checkUserAuth(authToken)) {
            throw new DataAccessException("User not logged in");
        }
        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(0, null, null, gameName, newGame);
        return gameDAO.createNewGame(gameData);
    }

    public void joinGame(String playerColor, String gameID, String authToken) throws DataAccessException, SQLException {
//        System.out.println("DEBUG: GameService playerColor = " + (playerColor == null ? "null" : "'" + playerColor + "'"));

        if (!authDAO.checkUserAuth(authToken)) {
            throw new DataAccessException("User not logged in");
        }
        if (gameID == null){
            throw new DataAccessException("Invalid Game ID");
        }
        if (!gameExists(Integer.parseInt(gameID), authToken)) {
            throw new DataAccessException("Game does not exist");
        }
//        if (playerColor != "BLACK"){
//            if (playerColor != "WHITE") {
//                return;
//            }
//        }
//        System.out.println("DEBUG: GameService playerColor = " + (playerColor == null ? "null" : "'" + playerColor + "'"));
        if (playerColor == null || playerColor.isEmpty()) {
            return;
            }
        if (Objects.equals(playerColor, "BLACK") || Objects.equals(playerColor, "WHITE")) {
            int gameIDint = Integer.parseInt(gameID);
            String username = authDAO.returnUsername(authToken);
            if (Objects.equals(playerColor, "WHITE")) {
                gameDAO.joinWhiteTeam(username, gameIDint);
            } else {
                gameDAO.joinBlackTeam(username, gameIDint);
            }
        } else{
            throw new DataAccessException("Please choose Black team or White team");
        }
    }

    public Map<Integer, GameData> listGames(String authToken) throws DataAccessException, SQLException {
        if (!authDAO.checkUserAuth(authToken)) {
            throw new DataAccessException("User not logged in");
        }
        return gameDAO.listGames();
    }

    public void clearData() throws DataAccessException {
        gameDAO.clearGames();
    }

    public boolean gameExists(int gameID, String authtoken) throws SQLException, DataAccessException {
        return listGames(authtoken).containsKey(gameID);
    }
}
