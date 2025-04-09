package websocket.commands;

import chess.ChessMove;

// Created this class becuase it says not to edit the og UserGameCommand
public class UserMoveCommand extends UserGameCommand {
    private final ChessMove move;

    public UserMoveCommand(String authToken, Integer gameID, ChessMove move) {
        // makes parent class UserGameCommand
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }
    public ChessMove getMove() {
        return move;
    }
}
