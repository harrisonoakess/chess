package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import exception.ResponseException;
import model.*;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ChessClient {
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken = null;

    public ChessClient(String serverUrl) {
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
            case "create" -> createGame(params);
            case "join" -> joinGame(params);
            case "list" -> listGames(params);
            case "observe" -> observeGame(params);
            case "logout" -> logout(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String register(String... params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException(400, "Expected: register <username> <password> <email>");
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
    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: create <Game name>");
        }
        CreateGameResult game = server.createGame(authToken, params[0]);
        return String.format("Created game: %s", game.gameID());
    }
    public String joinGame(String... params) throws ResponseException{
        assertSignedIn();
        if (params.length < 1 || params.length > 2) {
            throw new ResponseException(400, "Expected: join <game ID> <WHITE or BLACK>");
        }
        String color;
        if (params.length == 2) {
            color = params[1].toUpperCase();
        }else {
            color = null;
        }
        if (!Objects.equals(color, "WHITE") && !Objects.equals(color, "BLACK") && !Objects.equals(color, null)) {
            throw new ResponseException(400, "Color must be WHITE or BLACK");
        }
        server.joinGame(color, params[0], authToken);
        ChessGame game = new ChessGame();
        String perspective = color != null && color.equals("BLACK") ? "BLACK" : "WHITE";
        return "Joined game " + params[0] + (color != null ? " as " + color : " as observer") + "\n" + makeBoard(game.getBoard(), perspective);
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: observe <game ID>");
        }
//        server.joinGame(null, params[0], authToken);
        ChessGame game = new ChessGame();
        return String.format("Observing game: %s\n", params[0]) + makeBoard(game.getBoard(), "WHITE");
    }
    public String listGames(String... params) throws ResponseException {
        assertSignedIn();
        ListAllGamesResult games = server.listGames(authToken);
        if (games.games().length == 0) {
            return "No games available.";
        }
        StringBuilder result = new StringBuilder("Games:\n");
        for (int i = 0; i< games.games().length; i++) {
            GameData game = games.games()[i];
            result.append(String.format("Game name: %s, Game id: %s, White player: %s, Black player: %s",
                    game.gameName(), game.gameID(), game.whiteUsername(), game.blackUsername()));
            result.append("\n");
        }
        return result.toString();
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <username> <password> <email>
                    login <username> <password>
                    help
                    quit
                    """;
        } else {
            return """
                    create <name of game>
                    list
                    join <game ID> <BLACK or WHITE>
                    observe <game id>
                    logout
                    help
                    quit
                    """;
        }
    }
    private String makeBoard(ChessBoard board, String playerColor) {
        StringBuilder stringBoard = new StringBuilder();
        if ("WHITE".equals(playerColor)) {
            // White perspective
            stringBoard.append("   a   b   c  d   e   f  g  h\n");
            for (int row = 8; row >= 1; row--) {
                stringBoard.append(row).append(" ");
                for (int col = 1; col <= 8; col++) {
                    String color = getColor(row,col);
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    stringBoard.append(color).append(getPieceSymbol(piece)).append(RESET_BG_COLOR);
                }
                stringBoard.append(" ").append(row).append("\n");
            }
            stringBoard.append("   a   b   c  d   e   f  g  h\n");
        } else {
            // Black perspective
            stringBoard.append("   h   g   f  e   d   c  b  a\n");
            for (int row = 1; row <= 8; row++) {
                stringBoard.append(row).append(" ");
                for (int col = 8; col >= 1; col--) {
                    String color = getColor(row,col);
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    stringBoard.append(color).append(getPieceSymbol(piece)).append(RESET_BG_COLOR);
                }
                stringBoard.append(" ").append(row).append("\n");
            }
            stringBoard.append("   h   g   f  e   d   c  b  a\n");
        }
        return stringBoard.toString();
    }

    private String getColor(int row, int col) {
        String color;
        if ((row + col) % 2 == 0) {
            color = SET_BG_COLOR_DARK_GREY;
        }else {
            color = SET_BG_COLOR_LIGHT_GREY;
        }
        return color;
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_KING;
            }
            else {
                return BLACK_KING;}
        }

        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_QUEEN;
            }
            else {
                return BLACK_QUEEN;
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_BISHOP;
            }
            else {
                return BLACK_BISHOP;
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_KNIGHT;
            }
            else {
                return BLACK_KNIGHT;
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_ROOK;
            }
            else {
                return BLACK_ROOK;
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_PAWN;
            }
            else {
                return BLACK_PAWN;
            }
        }
        return null;
    }


    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    public boolean isLoggedIn() {
        return authToken == null;
    }
}