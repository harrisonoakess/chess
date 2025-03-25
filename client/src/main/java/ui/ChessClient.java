package ui;

import client.ServerFacade;
import exception.ResponseException;

import java.util.Arrays;

public class ChessClient {
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private final String serverUrl;

    public ChessClient(ServerFacade server, String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        if (state == State.SIGNEDOUT) {
            return evalLoggedOut(cmd, params);
        } else {
            return evalLoggedIn(cmd, params);
        }

    }

    public String evalLoggedOut(String cmd, String... params) { // the three periods means it can take
        return switch (cmd) {                                   // a different amount of params each time
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String evalLoggedIn(String cmd, String... params) { // the three periods means it can take
        return switch (cmd) {                                  // a different amount of params each time
            case "create game" -> createGame(params);
            case "join game" -> joinGame(params);
            case "list games" -> listGames(params);
            case "logout" -> logout(params);
            case "quit" -> "quit";
            default -> help();
        };

    }
}
