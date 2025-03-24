package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.*;
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
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
}
