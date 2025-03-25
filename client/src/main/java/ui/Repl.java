package ui;

import exception.ResponseException;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverURL) {
        client = new ChessClient(serverURL);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to my chess game. Sign in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println("Goodbye");
    }

    private void printPrompt() {
        String logState;
        if (authToken == null) {
            logState = "[Logged out]";
        } else {
            logState = "[Logged in]";
        }
        System.out.print("\n" + RESET_TEXT_COLOR + SET_TEXT_COLOR_WHITE + logState + SET_TEXT_COLOR_GREEN);
    }

    private String authToken = null;
    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String eval(String input) throws ResponseException {
        String result = client.eval(input);
        if (input.startsWith("login") || input.startsWith("register")) {
            try {
                authToken = client.eval(input).contains("Logged in") || client.eval(input).contains("Registered") ? "loggedIn" : null;
            } catch (ResponseException ignored) {
                authToken = null;
            }
        } else if (input.startsWith("logout")) {
            authToken = null;
        }
        return result;
    }



}



