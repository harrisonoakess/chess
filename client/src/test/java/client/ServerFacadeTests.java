package client;

import exception.ResponseException;
import model.LoginResult;
import model.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;


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


}
