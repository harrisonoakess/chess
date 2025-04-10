package ui;

import chess.*;
import client.ServerFacade;
import exception.ResponseException;
import model.*;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessageExtended;


import java.util.Arrays;

import static ui.EscapeSequences.*;

public class ChessClient implements NotificationHandler{
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken = null;
    private WebSocketFacade webSocketFacade;
    private String currentGameID = null;
    private String playerColor = null;
    private ChessGame currentGame = null;

    public ChessClient(String serverUrl) throws ResponseException {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.webSocketFacade = new WebSocketFacade(serverUrl, this);
    }

    @Override
    public void notify(ServerMessageExtended serverMessageExtended) {
        switch (serverMessageExtended.getServerMessageType()) {
            case LOAD_GAME:
                currentGame = serverMessageExtended.game;
                System.out.println("\nGame updated:\n" + makeBoard(currentGame.getBoard(), playerColor != null ? playerColor : "WHITE"));
                break;
            case NOTIFICATION:
                System.out.println("\n" + SET_TEXT_COLOR_YELLOW + serverMessageExtended.message + RESET_TEXT_COLOR);
                break;
            case ERROR:
                System.out.println("\n" + SET_TEXT_COLOR_RED + serverMessageExtended.errorMessage + RESET_TEXT_COLOR);
                break;
        }
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

    public String evalGameplay(String cmd, String... params) throws ResponseException {
        return switch (cmd) {
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "move" -> makeMove(params);
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
            currentGameID = params[0];
            playerColor = color;
            currentGame = new ChessGame();
            state = state.GAMEPLAY;
            webSocketFacade.connect(currentGameID, authToken);
            String perspective = color != null && color.equals("BLACK") ? "BLACK" : "WHITE";
            return "Joined game " + params[0] + (color != null ? " as " + color : " as observer") + "\n" + makeBoard(currentGame.getBoard(), perspective);
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
        return switch (state) {
            case SIGNEDOUT -> """
                    register <username> <password> <email>
                    login <username> <password>
                    help
                    quit
                    """;
            case SIGNEDIN -> """
                    create <name of game>
                    list
                    join <game ID> <BLACK or WHITE>
                    observe <game id>
                    logout
                    help
                    quit
                    """;
            case GAMEPLAY -> """
                    redraw
                    leave
                    move <start> <end> <promotion piece(if applicable)>
                    resign
                    highlight
                    help
                    """;
        };
    }


    private String redraw() throws ResponseException {
        try{
            String perspective = playerColor != null && playerColor.equals("BLACK") ? "BLACK" : "WHITE";
            return makeBoard(currentGame.getBoard(), perspective);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String makeMove(String... params) throws ResponseException {
        try {
            ChessPiece.PieceType promotionPiece = null;
            if (params[0].length() != 2 && params[1].length() != 2) {
                throw new IllegalArgumentException("Not a valid move");
            }
            ChessPosition startingPosition = stringToMove(params[0]);
            ChessPosition endPosition = stringToMove(params[1]);
            if (params.length == 3) {
                promotionPiece = stringToPromotionalPiece(params[2]);
            }
            ChessMove newMove = new ChessMove(startingPosition, endPosition, promotionPiece);
            webSocketFacade.makeMove(currentGameID, authToken, newMove);
            return "";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ChessPosition stringToMove(String position) {
        char colChar = position.charAt(0);
        char rowChar = position.charAt(1);
        int col = colChar - 'a' + 1;
        int row = rowChar- '0';
        if (col < 1 || col > 8 || row < 1 || row > 8) throw new IllegalArgumentException("Position is not on the baord");
        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType stringToPromotionalPiece(String piece) {
    piece = piece.toLowerCase();
    return switch (piece) {
        case "queen" -> ChessPiece.PieceType.QUEEN;
        case "knight" -> ChessPiece.PieceType.KNIGHT;
        case "bishop" -> ChessPiece.PieceType.BISHOP;
        case "rook" -> ChessPiece.PieceType.ROOK;
        default -> throw new IllegalArgumentException("Invalid Piece");
        };
    }



    private String leave() throws ResponseException {
        try {
            webSocketFacade.leave(currentGameID, authToken);
            currentGameID = null;
            playerColor = null;
            currentGame = null;
            state = State.SIGNEDIN;
            return "You have left the game";
        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }
    private String resign() throws ResponseException {
        try{
            webSocketFacade.resign(currentGameID, authToken);
            return "You have resigned";
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    private String highlightMoves(String ... params) throws ResponseException {
        return "";
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