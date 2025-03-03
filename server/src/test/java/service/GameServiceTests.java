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

import java.util.Map;

public class GameServiceTests {
    private DBAuthDAO authDAO;
    private DBGameDAO gameDAO;
    private GameService gameService;
    private String authToken;
    private final String username = "fake_user";

    @BeforeEach
    public void testReset() throws DataAccessException {
        authDAO = new DBAuthDAO();
        gameDAO = new DBGameDAO(authDAO);
        gameService = new GameService(gameDAO, authDAO);
        String username = "fake_user";
        AuthData authData = authDAO.createUserAuth(username);
        authToken = authData.authToken();
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




}
