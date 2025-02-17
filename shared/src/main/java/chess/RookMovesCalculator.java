package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends MoveHelpers {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        addMoves(board, myPosition, validMoves, 0, 1);
        addMoves(board, myPosition, validMoves, 0, -1);
        addMoves(board, myPosition, validMoves, 1, 0);
        addMoves(board, myPosition, validMoves, -1, 0);

        return validMoves;
    }
}
