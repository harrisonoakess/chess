package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMovesCalculator implements PieceMovesCalculator{
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
        int[] rowUpLeft = {1, 2, 3, 4, 5, 6, 7};
        int[] colUpLeft = {-1,-2,-3,-4,-5,-6,-7};

        for (int i = 0; i < rowUpLeft.length; i++) {
            int newRow = rowUpLeft[i] + currentRow;
            int newCol = colUpLeft[i] + currentCol;

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
        int[] rowUpRight = {1, 2, 3, 4, 5, 6, 7};
        int[] colUpRight = {1, 2, 3, 4, 5, 6, 7};

        for (int i = 0; i < rowUpRight.length; i++) {
            int newRow = rowUpRight[i] + currentRow;
            int newCol = colUpRight[i] + currentCol;

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
        int[] rowDownRight = {-1,-2,-3,-4,-5,-6,-7};
        int[] colDownRight = { 1, 2, 3, 4, 5, 6, 7};

        for (int i = 0; i < rowDownRight.length; i++) {
            int newRow = rowDownRight[i] + currentRow;
            int newCol = colDownRight[i] + currentCol;

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
        int[] rowDownLeft = {-1,-2,-3,-4,-5,-6,-7};
        int[] colDownLeft = {-1,-2,-3,-4,-5,-6,-7};

        for (int i = 0; i < rowDownLeft.length; i++) {
            int newRow = rowDownLeft[i] + currentRow;
            int newCol = colDownLeft[i] + currentCol;

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

