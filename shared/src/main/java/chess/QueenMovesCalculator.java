package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMovesCalculator extends MoveHelpers {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        addMoves(board, myPosition, validMoves, 0, 1);
        addMoves(board, myPosition, validMoves, 0, -1);
        addMoves(board, myPosition, validMoves, 1, 0);
        addMoves(board, myPosition, validMoves, -1, 0);
        addMoves(board, myPosition, validMoves, 1, 1);
        addMoves(board, myPosition, validMoves, 1, -1);
        addMoves(board, myPosition, validMoves, -1, 1);
        addMoves(board, myPosition, validMoves, -1, -1);

        return validMoves;
    }
}
