package ui;

import org.eclipse.jetty.util.Scanner;

import static java.awt.Color.BLUE;
import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverURL) {
        client = new ChessClient(serverURL);
    }
    private String authToken = null;
    public void setAuthToken(String token) {
        this.authToken = token;
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
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println("Goodbye");
    }






    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + SET_TEXT_COLOR_WHITE + "Please type a command" + SET_TEXT_COLOR_GREEN);    }
}

