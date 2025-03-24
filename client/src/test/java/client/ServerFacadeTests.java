package client;

import exception.ResponseException;
import model.CreateGameResult;
import model.ListAllGamesResult;
import model.LoginResult;
import model.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;

import java.awt.*;


public class ServerFacadeTests {

    private static Server server;
    private static String serverUrl;
    private ServerFacade facade;


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverUrl = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setUp() throws ResponseException {
        facade = new ServerFacade(serverUrl);
        facade.clear();
    }


    @Test
    @DisplayName("register success")
    public void registerSuccess() throws ResponseException {
        RegisterResult result = facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        Assertions.assertEquals("fakeuser", result.username());
    }

    @Test
    @DisplayName("register fail: duplicate account")
    public void registerFailure() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
            facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        });
    }
    @Test
    @DisplayName("login success")
    public void loginSuccess() throws ResponseException {
        facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        LoginResult result = facade.login("fakeuser", "fakePass");
        Assertions.assertEquals("fakeuser", result.username());
    }
    @Test
    @DisplayName("login fail: wrong password")
    public void loginFailure() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
            facade.login("fakeuser", "wrongPass");
        });
    }
    @Test
    @DisplayName("logout success")
    public void logoutSuccess() throws ResponseException {
        RegisterResult result = facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        facade.logout(result.authToken());
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.createGame("wrongAuth", "newGame");
        });
    }
    @Test
    @DisplayName("logout fail: user not logged in")
    public void logoutFailure() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.logout("no auth");
        });
    }
    @Test
    @DisplayName("create game success")
    public void createGameSuccess() throws ResponseException {
        RegisterResult result = facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        CreateGameResult gameResult = facade.createGame(result.authToken(), "newGame");
        Assertions.assertNotNull(gameResult.gameID());
    }
    @Test
    @DisplayName("create game fail: user not authorized")
    public void createGameFail() throws ResponseException {
        RegisterResult result = facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        Assertions.assertThrows(ResponseException.class, () -> {
            CreateGameResult gameResult = facade.createGame("badAuth", "newGame");
        });
    }
    @Test
    @DisplayName("join game success")
    public void joinGameSuccess() throws ResponseException {
        RegisterResult result = facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        CreateGameResult gameResult = facade.createGame(result.authToken(), "newGame");
        facade.joinGame("BLACK", String.valueOf(gameResult.gameID()), result.authToken());
        ListAllGamesResult games = facade.listGames(result.authToken());
        Assertions.assertEquals("fakeuser",games.games()[0].blackUsername());
    }
    @Test
    @DisplayName("join game fail: invalid color")
    public void joinGameFail() throws ResponseException {
        RegisterResult result = facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        CreateGameResult gameResult = facade.createGame(result.authToken(), "newGame");
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.joinGame("RED", String.valueOf(gameResult.gameID()), result.authToken());
        });
    }
    @Test
    @DisplayName("list games success")
    public void listGamesSuccess() throws ResponseException {
        RegisterResult result = facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        CreateGameResult gameResult = facade.createGame(result.authToken(), "newGame");
        ListAllGamesResult games = facade.listGames(result.authToken());
        Assertions.assertEquals(1, games.games().length);
    }
    @Test
    @DisplayName("list game fail: invalid auth")
    public void listGamesFail() throws ResponseException {
        RegisterResult result = facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        CreateGameResult gameResult = facade.createGame(result.authToken(), "newGame");
        Assertions.assertThrows(ResponseException.class, () -> {
            ListAllGamesResult games = facade.listGames("badAuth");
        });
    }
    @Test
    @DisplayName("clear data success")
    public void clearSuccess() throws ResponseException {
        RegisterResult result = facade.register("fakeuser", "fakePass", "fakeEmail@fake.org");
        facade.clear();
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login("fakeuser", "wrongPass");
        });}
}
