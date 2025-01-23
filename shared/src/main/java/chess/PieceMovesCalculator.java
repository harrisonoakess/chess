package chess;

import java.util.Collection;

// ChessBoard board passes in the current board to help see where pieces can go
// ChessPosition myPosition passes in current piece position
public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}