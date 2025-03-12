package service;

import dataaccess.DataAccessException;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import dataaccess.datastorage.DBUserDAO;
import model.CreateGameResult;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameService gameService;
    private DBAuthDAO authDAO;
    private DBGameDAO gameDAO;

    @BeforeEach
    public void gameSetup() throws SQLException, DataAccessException {
        authDAO = new DBAuthDAO();
        gameDAO = new DBGameDAO();
        gameService = new GameService(gameDAO, authDAO);
        gameService.clearData();
    }

    @Test
    @DisplayName("Create game")
    public void testCreateGame() throws Exception {
        String authToken = authDAO.createUserAuth("test_user").authToken();
        CreateGameResult result = gameService.createGame("test_game", authToken);

        Map<Integer, GameData> games = gameDAO.listGames();
        assertTrue(games.containsKey(result.gameID()));
    }
    @Test
    @DisplayName("Join white team")
    public void testJoinWhiteTeam() throws Exception {
        String authToken = authDAO.createUserAuth("test_user").authToken();
        CreateGameResult result = gameService.createGame("test_game", authToken);

        gameService.joinGame("WHITE", String.valueOf(result.gameID()), authToken);

        GameData game = gameDAO.listGames().get(result.gameID());
        Assertions.assertEquals("test_user", game.whiteUsername());
    }
    @Test
    @DisplayName("Join black team")
    public void testJoinBlackTeam() throws Exception {
        String authToken = authDAO.createUserAuth("test_user").authToken();
        CreateGameResult result = gameService.createGame("test_game", authToken);

        gameService.joinGame("BLACK", String.valueOf(result.gameID()), authToken);

        GameData game = gameDAO.listGames().get(result.gameID());
        Assertions.assertEquals("test_user", game.blackUsername());
    }
    @Test
    @DisplayName("Join while not logged in")
    public void testJoinWhileNotLoggedIn() throws Exception {
        String authToken = authDAO.createUserAuth("test_user").authToken();
        CreateGameResult result = gameService.createGame("test_game", authToken);

        Exception exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame("WHITE", String.valueOf(result.gameID()), "bad token");
        });
        assertEquals("User not logged in", exception.getMessage());
    }
    @Test
    @DisplayName("ID left null")
    public void testInvalidGameID() throws Exception {
        String authToken = authDAO.createUserAuth("test_user").authToken();
        Exception exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame("WHITE", null, authToken);
        });

        assertEquals("Invalid Game ID", exception.getMessage());
    }
}
