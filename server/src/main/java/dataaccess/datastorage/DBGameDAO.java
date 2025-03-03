package dataaccess.datastorage;

import dataaccess.DataAccessException;
import model.CreateGameResult;
import model.GameData;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class DBGameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private final DBAuthDAO authDAO;
    private int nextGameID = 1;

    public DBGameDAO(DBAuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public CreateGameResult createNewGame(GameData gameInfo) throws DataAccessException {
        int gameID = nextGameID++;
        GameData updatedGameData = new GameData(gameID, gameInfo.whiteUsername(),
                gameInfo.blackUsername(),
                gameInfo.gameName(),
                gameInfo.game());
        games.put(gameID, updatedGameData);

        return new CreateGameResult(gameID);
    }

    public void joinWhiteTeam(String username, Integer gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game not found");
        }
        GameData game = games.get(gameID);
        if (game.whiteUsername() != null) {
            throw new DataAccessException("Team already filled");
        }
        GameData updatedGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
        games.put(gameID, updatedGame);
    }

    public void joinBlackTeam(String username, Integer gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game not found");
        }
        GameData game = games.get(gameID);
        if (game.blackUsername() != null) {
            throw new DataAccessException("Team already filled");
        }
        GameData updatedGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
        games.put(gameID, updatedGame);
    }

    public Map<Integer, GameData> listGames() throws DataAccessException {
        return new HashMap<>(games);
    }

    public void clearGames(){
        games.clear();
    }
}
