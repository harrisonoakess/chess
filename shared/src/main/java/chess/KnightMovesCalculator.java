package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        // Gets the position of the current piece
        int position_row = myPosition.getRow();
        int position_col = myPosition.getColumn();

        // Rules for the King
        int[] optionalRowMoves = {1, 1,-1,-1, 2, 2,-2,-2};
        int[] optionalColMoves = {2,-2, 2,-2, 1,-1, 1,-1};

        // takes into account where the starting position of the piece is
        for (int i = 0; i < optionalRowMoves.length; i++) {
            int positionalRowMoves = (position_row + optionalRowMoves[i])+1;
            int positionalColMoves = (position_col + optionalColMoves[i])+1;

            // creates a new position bases off optional moves
            ChessPosition pieceNewPosition =  new ChessPosition(positionalRowMoves, positionalColMoves);

            // checks to see if it's on the board
            if (pieceNewPosition.getRow() < 8 && pieceNewPosition.getColumn() < 8 && pieceNewPosition.getRow() >= 0 && pieceNewPosition.getColumn() >= 0){
                // Check to see if the spot is empty or has an enemy piece
                if (board.getPiece(pieceNewPosition) == null || board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    // Look on "ChessMove.java" line 30 for this. Adds the move to the array
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                }
            }


        }
        return validMoves;
    }
}
