package chess;

import java.util.Collection;

public abstract class MoveHelpers implements PieceMovesCalculator {

    void addMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int col, int row) {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        for (int i = 1; i <= 7; i++) {
            int rowCheck = currentRow + row * i;
            int colCheck = currentCol + col * i;

            ChessPosition newPosition = new ChessPosition(rowCheck, colCheck);
            if (rowCheck < 1 || rowCheck > 8 || colCheck < 1 || colCheck > 8) {
                break;
            }
            if (board.getPiece(newPosition) == null) {
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            } else if (board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                validMoves.add(new ChessMove(myPosition, newPosition, null));
                break;
            } else {
                break;
            }
        }
    }
}
