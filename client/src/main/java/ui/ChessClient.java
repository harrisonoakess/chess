package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessMove;
import chess.ChessPosition;
import client.ServerFacade;
import exception.ResponseException;
import model.*;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.NotificationHandler;


import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ChessClient{
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken = null;
    private WebSocketFacade webSocketFacade;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.webSocketFacade = new WebSocketFacade(serverUrl, this);
    }

    public String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (state) {
            case SIGNEDOUT -> evalLoggedOut(cmd, params);
            case SIGNEDIN -> evalLoggedIn(cmd, params);
            case GAMEPLAY -> evalGameplay(cmd, params);
        };
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

    public String evalGameplay(String cmd, String... paramas) throws ResponseException {
        return switch (cmd) {
            case "help" -> helpGameplay();
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "move" -> makeMove(parmas);
            case "resign" -> resign();
            case "highlight" -> highlightMoves(params);
            default -> help();
        };
    }

    public String register(String... params) throws ResponseException {
        try {
            if (params.length != 3) {
                throw new ResponseException(400, "Expected: register <username> <password> <email>");
            }
            RegisterResult result = server.register(params[0], params[1], params[2]);
            authToken = result.authToken();
            state = State.SIGNEDIN;
            return String.format("Registered as %s", result.username());
        } catch (ResponseException e) {
            return switch (e.statusCode()) {
                case 400 -> "Error: Invalid input (username, password, or email cannot be blank)";
                case 403 -> "Error: Username already taken";
                case 500 -> "Error: Server error - registration failed";
                default -> "Error: Unknown error during registration - " + e.getMessage();
            };
        }
    }

    public String login(String... params) {
        if (params.length != 2) {
            return "Error: Expected: login <username> <password>";
        }
        try {
            LoginResult result = server.login(params[0], params[1]);
            authToken = result.authToken();
            state = State.SIGNEDIN;
            return String.format("Logged in as %s, type 'help' for more.", result.username());
        } catch (ResponseException e) {
            return switch (e.statusCode()) {
                case 401 -> {
                    if (e.getMessage().contains("user does not exist")) {
                        yield "Error: Username does not exist";
                    } else if (e.getMessage().contains("password does not match")) {
                        yield "Error: Incorrect password";
                    } else {
                        yield "Error: Unauthorized - " + e.getMessage();
                    }
                }
                case 500 -> "Error: Server error - login failed";
                default -> "Error: Unknown error during login - " + e.getMessage();
            };
        }
    }

    public String logout(String... params) {
        try {
            assertSignedIn();
            server.logout(authToken);
            authToken = null;
            state = State.SIGNEDOUT;
            return "Logged out";
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }
    public String createGame(String... params) throws ResponseException {
        try {
            assertSignedIn();
            if (params.length != 1) {
                throw new ResponseException(400, "Expected: create <Game name>");
            }
            CreateGameResult game = server.createGame(authToken, params[0]);
            return String.format("Created game: %s", game.gameID());
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String joinGame(String... params) throws ResponseException{
        try {
            assertSignedIn();
            if (params.length < 1 || params.length > 2) {
                throw new ResponseException(400, "Expected: join <game ID> <WHITE or BLACK>");
            }
            String color;
            if (params.length == 2) {
                color = params[1].toUpperCase();
            } else {
                color = null;
            }
            if (!params[0].matches("\\d+")) {
                return "Game ID must be a number";
            }
            if (color != null && !color.equals("WHITE") && !color.equals("BLACK")) {
                throw new ResponseException(400, "Color must be WHITE or BLACK");
            }
            server.joinGame(color, params[0], authToken);
            ChessGame game = new ChessGame();
            String perspective = color != null && color.equals("BLACK") ? "BLACK" : "WHITE";
            return "Joined game " + params[0] + (color != null ? " as " + color : " as observer") + "\n" + makeBoard(game.getBoard(), perspective);
        } catch (ResponseException e) {
            if (e.statusCode() == 400 && e.getMessage().contains("Invalid Game ID")) {
                return "game not found";
            }
            return e.getMessage();
        }
    }

    public String observeGame(String... params) throws ResponseException {
        try {
            assertSignedIn();
            if (params.length != 1) {
                throw new ResponseException(400, "Expected: observe <game ID>");
            }
            if (!params[0].matches("\\d+")) {
                return "Game ID must be a number";
            }
        server.joinGame(null, params[0], authToken);
            ChessGame game = new ChessGame();
            return String.format("Observing game: %s\n", params[0]) + makeBoard(game.getBoard(), "WHITE");
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }
    public String listGames(String... params) throws ResponseException {
        try {
            assertSignedIn();
            ListAllGamesResult games = server.listGames(authToken);
            if (games.games().length == 0) {
                return "No games available.";
            }
            StringBuilder result = new StringBuilder("Games:\n");
            for (int i = 0; i < games.games().length; i++) {
                GameData game = games.games()[i];
                result.append(String.format("Game name: %s, Game id: %s, White player: %s, Black player: %s",
                        game.gameName(), game.gameID(), game.whiteUsername(), game.blackUsername()));
                result.append("\n");
            }
            return result.toString();
        } catch (ResponseException e) {
            return e.getMessage();
        }
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
        boolean isWhite = "WHITE".equals(playerColor);

        appendColumnLabels(stringBoard, isWhite);
        appendBoardRows(stringBoard, board, isWhite);
        appendColumnLabels(stringBoard, isWhite);

        return stringBoard.toString();
    }

    private void appendColumnLabels(StringBuilder stringBoard, boolean isWhite) {
        String columns = isWhite ? "   a   b   c  d   e   f  g  h\n" :
                "   h   g   f  e   d   c  b  a\n";
        stringBoard.append(columns);
    }

    private void appendBoardRows(StringBuilder stringBoard, ChessBoard board, boolean isWhite) {
        int startRow = isWhite ? 8 : 1;
        int endRow = isWhite ? 1 : 8;
        int rowIncrement = isWhite ? -1 : 1;

        for (int row = startRow; isWhite ? row >= endRow : row <= endRow; row += rowIncrement) {
            stringBoard.append(row).append(" ");
            appendRowPieces(stringBoard, board, row, isWhite);
            stringBoard.append(" ").append(row).append("\n");
        }
    }

    private void appendRowPieces(StringBuilder stringBoard, ChessBoard board, int row, boolean isWhite) {
        int startCol = isWhite ? 1 : 8;
        int endCol = isWhite ? 8 : 1;
        int colIncrement = isWhite ? 1 : -1;

        for (int col = startCol; isWhite ? col <= endCol : col >= endCol; col += colIncrement) {
            String color = getColor(row, col);
            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            stringBoard.append(color).append(getPieceSymbol(piece)).append(RESET_BG_COLOR);
        }
    }

    private String getColor(int row, int col) {
        String color;
        if ((row + col) % 2 == 0) {
            color = SET_BG_COLOR_DARK_GREY;
        } else {
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