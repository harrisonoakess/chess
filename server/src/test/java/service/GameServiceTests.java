package service;

import dataaccess.DataAccessException;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import model.AuthData;
import model.CreateGameResult;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceTests {
    private DBAuthDAO authDAO;
    private DBGameDAO gameDAO;
    private GameService gameService;
    private String authToken;
    private final String username = "fake_user";

    @BeforeEach
    public void testReset() throws DataAccessException, SQLException {
        authDAO = new DBAuthDAO();
        gameDAO = new DBGameDAO();
        gameService = new GameService(gameDAO, authDAO);
        String username = "fake_user";
        String newAuth = java.util.UUID.randomUUID().toString();
        AuthData authData = authDAO.createUserAuth(username);
        authToken = authData.authToken();
        gameService.clearData();
    }

    @Test
    @DisplayName("Create game")
    public void testCreateGame() throws Exception {
        String gameName = "fake_game";
        CreateGameResult result = gameService.createGame(gameName, authToken);

        Map<Integer, GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.containsKey(result.gameID()));
    }
    @Test
    @DisplayName("Join white team")
    public void testJoinWhiteTeam() throws Exception {
        String gameName = "fake_game";
        CreateGameResult result = gameService.createGame(gameName, authToken);

        gameService.joinGame("WHITE", String.valueOf(result.gameID()), authToken);

        GameData game = gameDAO.listGames().get(result.gameID());
        Assertions.assertEquals(username, game.whiteUsername());
    }

    @Test
    @DisplayName("Join black team")
    public void testJoinBlackTeam() throws Exception {
        String gameName = "fake_game";
        CreateGameResult result = gameService.createGame(gameName, authToken);

        gameService.joinGame("BLACK", String.valueOf(result.gameID()), authToken);

        GameData game = gameDAO.listGames().get(result.gameID());
        Assertions.assertEquals(username, game.blackUsername());
    }

    @Test
    @DisplayName("Join while not logged in")
    public void testJoinWhileNotLoggedIn() throws Exception {
        String gameName = "fake_game";
        CreateGameResult result = gameService.createGame(gameName, authToken);

        Exception exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame("WHITE", String.valueOf(result.gameID()), "bad token");
        });
        assertEquals("User not logged in", exception.getMessage());
    }
    @Test
    @DisplayName("ID left null")
    public void testInvalidGameID() throws Exception {
        String gameName = "fake_game";
        CreateGameResult result = gameService.createGame(gameName, authToken);

        Exception exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame("WHITE", null, authToken);
        });

        assertEquals("Invalid Game ID", exception.getMessage());
    }
}
