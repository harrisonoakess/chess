package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.LoginResult;
import model.RegisterResult;

import java.util.Arrays;

public class ChessClient {
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken = null;

    public ChessClient(ServerFacade server, String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        if (state == State.SIGNEDOUT) {
            return evalLoggedOut(cmd, params);
        } else {
            return evalLoggedIn(cmd, params);
        }

    }

    public String evalLoggedOut(String cmd, String... params) throws ResponseException { // the three periods means it can take
        return switch (cmd) {                                                           // a different amount of params each time
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String evalLoggedIn(String cmd, String... params) throws ResponseException{ // the three periods means it can take
        return switch (cmd) {                                                           // a different amount of params each time
            case "create game" -> createGame(params);
            case "join game" -> joinGame(params);
            case "list games" -> listGames(params);
            case "logout" -> logout(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String register(String... params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException(400, "Expected: register <username> <password? <email>");
        }
        RegisterResult result = server.register(params[0], params[1], params[2]);
        authToken = result.authToken();
        state = State.SIGNEDIN;
        return String.format("Registered as %s", result.username());
    }

    public String login(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected: login <username> <password>");
        }
        LoginResult result = server.login(params[0], params[1]);
        authToken = result.authToken();
        state = State.SIGNEDIN;
        return String.format("Logged in as %s", result.username());
    }

    public String logout(String... params) throws ResponseException {
        assertSignedIn();
        server.logout(authToken);
        authToken = null;
        state = State.SIGNEDOUT;
        return "logged out";
    }











    public String help() {
        if (state == State.SIGNEDIN) {
            return """
                    register <username> <password? <email>
                    login <username> <password>
                    help
                    quit
                    """;
        } else {
            return """
                    create <name of game>
                    list
                    join <game ID> <BLACK or WHITE>
                    logout
                    help
                    quit
                    """;
        }
    }
    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
