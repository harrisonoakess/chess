package dataaccess;

import chess.ChessGame;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import dataaccess.datastorage.DBUserDAO;
import model.CreateGameResult;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLGameTests {
    private DBAuthDAO authDAO;
    private DBUserDAO userDAO;
    private DBGameDAO gameDAO;

    @BeforeEach
    public void setup() throws DataAccessException, SQLException {
        authDAO = new DBAuthDAO();
        userDAO = new DBUserDAO(authDAO);
        gameDAO = new DBGameDAO();

        authDAO.clearAuths();
        userDAO.clearUsers();
        gameDAO.clearGames();
    }

    @Test
    @DisplayName("Account added successfully")
    public void testCreateGameSuccess() throws DataAccessException, SQLException {
        GameData game = new GameData(0,
                null,
                null,
                "game1",
                new ChessGame());
        CreateGameResult result = gameDAO.createNewGame(game);
        Map<Integer, GameData> games = gameDAO.listGames();

        Assertions.assertEquals("game1", games.get(result.gameID()).gameName());
    }

    @Test
    @DisplayName("Create game with null game name")
    public void testCreateGamefail() throws DataAccessException, SQLException {
        GameData game = new GameData(0,
                null,
                null,
                null,
                new ChessGame());
        DataAccessException e = assertThrows(DataAccessException.class, () ->
                gameDAO.createNewGame(game));
        Assertions.assertTrue(e.getMessage().contains("Column 'gameName' cannot be null"));

    }

    @Test
    @DisplayName("Join white team")
    public void testJoinWhiteTeam() throws Exception {
        GameData game = new GameData(0,
                null,
                null,
                "game1",
                new ChessGame());
        CreateGameResult result = gameDAO.createNewGame(game);
        gameDAO.joinWhiteTeam("fake_user_white", result.gameID());
        GameData gameWithPlayer = gameDAO.listGames().get(result.gameID());

        Assertions.assertEquals("fake_user_white", gameWithPlayer.whiteUsername());
    }

    @Test
    @DisplayName("Join black team")
    public void testJoinBlackTeam() throws Exception {
        GameData game = new GameData(0,
                null,
                null,
                "game1",
                new ChessGame());
        CreateGameResult result = gameDAO.createNewGame(game);
        gameDAO.joinBlackTeam("fake_user_black", result.gameID());
        GameData gameWithPlayer = gameDAO.listGames().get(result.gameID());

        Assertions.assertEquals("fake_user_black", gameWithPlayer.blackUsername());
    }
    @Test
    @DisplayName("Team spot already taken")
    public void testJoinFullTeam() throws DataAccessException, SQLException {
        GameData game = new GameData(0,
                "fake_user_white",
                null,
                "game1",
                new ChessGame());
        CreateGameResult result = gameDAO.createNewGame(game);

        Exception e = assertThrows(DataAccessException.class, () ->
            gameDAO.joinWhiteTeam("another_fake_user", result.gameID()));
            Assertions.assertEquals("Team already filled", e.getMessage());
    }
    @Test
    @DisplayName("Show empty list")
    public void testEmptyGameList() throws DataAccessException, SQLException {
        Map<Integer, GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.isEmpty());
    }
    @Test
    @DisplayName("clear games")
    public void testClearGameData() throws DataAccessException, SQLException {
        GameData game = new GameData(0,
                "fake_user_white",
                null,
                "game1",
                new ChessGame());
        gameDAO.clearGames();

        Map<Integer, GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.isEmpty());
    }
}
