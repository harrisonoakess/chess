package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();


        int[] rowUp = {1, 2, 3, 4, 5, 6, 7};
        int[] colUp = {0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < rowUp.length; i++) {
            int newRow = rowUp[i] + currentRow;
            int newCol = colUp[i] + currentCol;

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (newPosition.getRow() > 0 && newPosition.getRow() < 9 && newPosition.getColumn() > 0 && newPosition.getColumn() < 9) {
                // if new position is null add it
                if (board.getPiece(newPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                }else{
                    break;
                }
            }
        }
        int[] rowRight = {0, 0, 0, 0, 0, 0, 0};
        int[] colRight = {1, 2, 3, 4, 5, 6, 7};

        for (int i = 0; i < rowRight.length; i++) {
            int newRow = rowRight[i] + currentRow;
            int newCol = colRight[i] + currentCol;

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (newPosition.getRow() > 0 && newPosition.getRow() < 9 && newPosition.getColumn() > 0 && newPosition.getColumn() < 9) {
                // if new position is null add it
                if (board.getPiece(newPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                }else{
                    break;
                }
            }
        }
        int[] rowDown = {-1, -2, -3, -4, -5, -6, -7};
        int[] colDown = {0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < rowDown.length; i++) {
            int newRow = rowDown[i] + currentRow;
            int newCol = colDown[i] + currentCol;

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (newPosition.getRow() > 0 && newPosition.getRow() < 9 && newPosition.getColumn() > 0 && newPosition.getColumn() < 9) {
                // if new position is null add it
                if (board.getPiece(newPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                }else{
                    break;
                }
            }
        }
        int[] rowLeft = { 0, 0, 0, 0, 0, 0, 0};
        int[] colLeft = {-1,-2,-3,-4,-5,-6,-7};

        for (int i = 0; i < rowLeft.length; i++) {
            int newRow = rowLeft[i] + currentRow;
            int newCol = colLeft[i] + currentCol;

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (newPosition.getRow() > 0 && newPosition.getRow() < 9 && newPosition.getColumn() > 0 && newPosition.getColumn() < 9) {
                // if new position is null add it
                if (board.getPiece(newPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                }else{
                    break;
                }
            }
        }
        return validMoves;
    }
}
