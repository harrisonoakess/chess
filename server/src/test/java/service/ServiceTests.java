package service;

import dataaccess.DataAccessException;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import dataaccess.datastorage.DBUserDAO;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private UserService userService;
    private GameService gameService;
    private DBAuthDAO authDAO;
    private DBUserDAO userDAO;
    private DBGameDAO gameDAO;

    @BeforeEach
    public void setup() throws SQLException, DataAccessException {
        authDAO = new DBAuthDAO();
        userDAO = new DBUserDAO(authDAO);
        gameDAO = new DBGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        userService.clearData();
        gameService.clearData();
    }
    @Test
    @DisplayName("Account added successfully")
    public void testRegisterSuccess() throws DataAccessException, SQLException {
        RegisterRequest request = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        RegisterResult result = userService.register(request);

        Assertions.assertEquals("fake_username", result.username());
    }
    @Test
    @DisplayName("User already exists")
    public void testUserAlreadyExists() throws DataAccessException, SQLException {
        RegisterRequest request = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(request);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
        assertEquals("Error: User already exists", exception.getMessage());
    }
    @Test
    @DisplayName("Password left blank")
    public void testBlankPassword() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("fake_username", null, "email@fake.gov");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
        assertEquals("Password cannot be blank", exception.getMessage());
    }
    @Test
    @DisplayName("Successful login")
    public void testSuccessfullyLogin() throws DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username", "fake_password");
        LoginResult loginResult = userService.login(loginRequest);

        assertEquals(loginRequest.username(), loginResult.username());
    }
    @Test
    @DisplayName("User does not exist")
    public void testUsernameNotFound() throws DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username_", "fake_password");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
        assertEquals("User does not exist", exception.getMessage());
    }
    @Test
    @DisplayName("Incorrect password")
    public void testIncorrectPassword() throws DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username", "fake_password_");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
        assertEquals("Password does not match", exception.getMessage());
    }
    @Test
    @DisplayName("successful logout")
    public void testLogout() throws DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username", "fake_password");
        LoginResult loginResult = userService.login(loginRequest);

        userService.logout(loginResult.authToken());
        assertFalse(authDAO.checkUserAuth(loginResult.authToken()));
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
