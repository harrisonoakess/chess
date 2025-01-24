package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Gets the position of the current piece
        int position_row = myPosition.getRow();
        int position_col = myPosition.getColumn();

        // up
        int[] UpRow = {0,0,0,0,0,0,0}; // -1,-2,-3,-4,-5,-6,-7, 1,2,3,4,5,6,7, -1,-2,-3,-4,-5,-6,-7};
        int[] UpCol = {1,2,3,4,5,6,7}; // -1,-2,-3,-4,-5,-6,-7, -1,-2,-3,-4,-5,-6,-7 ,1,2,3,4,5,6,7};

        // takes into account where the starting position of the piece is
        for (int i = 0; i < UpRow.length; i++) {
            int positionalRowMoves = (position_row + UpRow[i])+1;
            int positionalColMoves = (position_col + UpCol[i])+1;

            // creates a new position bases off optional moves
            ChessPosition pieceNewPosition =  new ChessPosition(positionalRowMoves, positionalColMoves);

            // checks to see if it's on the board
            if (pieceNewPosition.getRow() < 8 && pieceNewPosition.getColumn() < 8 && pieceNewPosition.getRow() >= 0 && pieceNewPosition.getColumn() >= 0){
                // Check to see if the spot is empty or has an enemy piece
                if (board.getPiece(pieceNewPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                } else if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                    break;

                    // Look on "ChessMove.java" line 30 for this. Adds the move to the array
//                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//                    if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
//                        break;
//                    }
                } else {
                    break;
                }
            }

        }

        // down
        int[] DownRow = { 0, 0, 0, 0, 0, 0, 0};
        int[] DownCol = {-1,-2,-3,-4,-5,-6,-7};

        // takes into account where the starting position of the piece is
        for (int i = 0; i < DownRow.length; i++) {
            int positionalRowMoves = (position_row + DownRow[i])+1;
            int positionalColMoves = (position_col + DownCol[i])+1;

            // creates a new position bases off optional moves
            ChessPosition pieceNewPosition =  new ChessPosition(positionalRowMoves, positionalColMoves);

            // checks to see if it's on the board
            if (pieceNewPosition.getRow() < 8 && pieceNewPosition.getColumn() < 8 && pieceNewPosition.getRow() >= 0 && pieceNewPosition.getColumn() >= 0){
                // Check to see if the spot is empty or has an enemy piece
                if (board.getPiece(pieceNewPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                } else if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                    break;

                    // Look on "ChessMove.java" line 30 for this. Adds the move to the array
//                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//                    if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
//                        break;
//                    }
                } else {
                    break;
                }
            }

        }

        // right
        int[] rightRow = {1,2,3,4,5,6,7};
        int[] rightCol = {0,0,0,0,0,0,0};

        // takes into account where the starting position of the piece is
        for (int i = 0; i < rightRow.length; i++) {
            int positionalRowMoves = (position_row + rightRow[i])+1;
            int positionalColMoves = (position_col + rightCol[i])+1;

            // creates a new position bases off optional moves
            ChessPosition pieceNewPosition =  new ChessPosition(positionalRowMoves, positionalColMoves);

            // checks to see if it's on the board
            if (pieceNewPosition.getRow() < 8 && pieceNewPosition.getColumn() < 8 && pieceNewPosition.getRow() >= 0 && pieceNewPosition.getColumn() >= 0){
                // Check to see if the spot is empty or has an enemy piece
                if (board.getPiece(pieceNewPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                } else if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                    break;

                    // Look on "ChessMove.java" line 30 for this. Adds the move to the array
//                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//                    if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
//                        break;
//                    }
                } else {
                    break;
                }
            }

        }

        // left and up
        int[] leftRow = {-1,-2,-3,-4,-5,-6,-7};
        int[] leftCol = { 0, 0, 0, 0, 0, 0, 0};

        // takes into account where the starting position of the piece is
        for (int i = 0; i < leftRow.length; i++) {
            int positionalRowMoves = (position_row + leftRow[i]) + 1;
            int positionalColMoves = (position_col + leftCol[i]) + 1;

            // creates a new position bases off optional moves
            ChessPosition pieceNewPosition = new ChessPosition(positionalRowMoves, positionalColMoves);

            // checks to see if it's on the board
            if (pieceNewPosition.getRow() < 8 && pieceNewPosition.getColumn() < 8 && pieceNewPosition.getRow() >= 0 && pieceNewPosition.getColumn() >= 0) {
                // Check to see if the spot is empty or has an enemy piece
                if (board.getPiece(pieceNewPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                } else if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                    break;

                    // Look on "ChessMove.java" line 30 for this. Adds the move to the array
//                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//                    if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
//                        break;
//                    }
                } else {
                    break;
                }
            }
        }

        // right and to the up
        int[] rightUpRow = {1,2,3,4,5,6,7}; // -1,-2,-3,-4,-5,-6,-7, 1,2,3,4,5,6,7, -1,-2,-3,-4,-5,-6,-7};
        int[] rightUpCol = {1,2,3,4,5,6,7}; // -1,-2,-3,-4,-5,-6,-7, -1,-2,-3,-4,-5,-6,-7 ,1,2,3,4,5,6,7};

        // takes into account where the starting position of the piece is
        for (int i = 0; i < rightUpRow.length; i++) {
            int positionalRowMoves = (position_row + rightUpRow[i])+1;
            int positionalColMoves = (position_col + rightUpCol[i])+1;

            // creates a new position bases off optional moves
            ChessPosition pieceNewPosition =  new ChessPosition(positionalRowMoves, positionalColMoves);

            // checks to see if it's on the board
            if (pieceNewPosition.getRow() < 8 && pieceNewPosition.getColumn() < 8 && pieceNewPosition.getRow() >= 0 && pieceNewPosition.getColumn() >= 0){
                // Check to see if the spot is empty or has an enemy piece
                if (board.getPiece(pieceNewPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                } else if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                    break;

                    // Look on "ChessMove.java" line 30 for this. Adds the move to the array
//                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//                    if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
//                        break;
//                    }
                } else {
                    break;
                }
            }

        }

        // left and down
        int[] leftDownRow = {-1,-2,-3,-4,-5,-6,-7};
        int[] leftDownCol = {-1,-2,-3,-4,-5,-6,-7};

        // takes into account where the starting position of the piece is
        for (int i = 0; i < leftDownRow.length; i++) {
            int positionalRowMoves = (position_row + leftDownRow[i])+1;
            int positionalColMoves = (position_col + leftDownCol[i])+1;

            // creates a new position bases off optional moves
            ChessPosition pieceNewPosition =  new ChessPosition(positionalRowMoves, positionalColMoves);

            // checks to see if it's on the board
            if (pieceNewPosition.getRow() < 8 && pieceNewPosition.getColumn() < 8 && pieceNewPosition.getRow() >= 0 && pieceNewPosition.getColumn() >= 0){
                // Check to see if the spot is empty or has an enemy piece
                if (board.getPiece(pieceNewPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                } else if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                    break;

                    // Look on "ChessMove.java" line 30 for this. Adds the move to the array
//                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//                    if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
//                        break;
//                    }
                } else {
                    break;
                }
            }

        }

        // right and down
        int[] rightDownRow = {1,2,3,4,5,6,7};
        int[] rightDownCol = {-1,-2,-3,-4,-5,-6,-7};

        // takes into account where the starting position of the piece is
        for (int i = 0; i < rightDownRow.length; i++) {
            int positionalRowMoves = (position_row + rightDownRow[i])+1;
            int positionalColMoves = (position_col + rightDownCol[i])+1;

            // creates a new position bases off optional moves
            ChessPosition pieceNewPosition =  new ChessPosition(positionalRowMoves, positionalColMoves);

            // checks to see if it's on the board
            if (pieceNewPosition.getRow() < 8 && pieceNewPosition.getColumn() < 8 && pieceNewPosition.getRow() >= 0 && pieceNewPosition.getColumn() >= 0){
                // Check to see if the spot is empty or has an enemy piece
                if (board.getPiece(pieceNewPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                } else if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                    break;

                    // Look on "ChessMove.java" line 30 for this. Adds the move to the array
//                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//                    if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
//                        break;
//                    }
                } else {
                    break;
                }
            }

        }

        // left and up
        int[] leftUpRow = {-1,-2,-3,-4,-5,-6,-7};
        int[] leftUpCol = {1,2,3,4,5,6,7};

        // takes into account where the starting position of the piece is
        for (int i = 0; i < leftUpRow.length; i++) {
            int positionalRowMoves = (position_row + leftUpRow[i]) + 1;
            int positionalColMoves = (position_col + leftUpCol[i]) + 1;

            // creates a new position bases off optional moves
            ChessPosition pieceNewPosition = new ChessPosition(positionalRowMoves, positionalColMoves);

            // checks to see if it's on the board
            if (pieceNewPosition.getRow() < 8 && pieceNewPosition.getColumn() < 8 && pieceNewPosition.getRow() >= 0 && pieceNewPosition.getColumn() >= 0) {
                // Check to see if the spot is empty or has an enemy piece
                if (board.getPiece(pieceNewPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                } else if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
                    break;

                    // Look on "ChessMove.java" line 30 for this. Adds the move to the array
//                    validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//                    if (board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
//                        break;
//                    }
                } else {
                    break;
                }
            }
        }
        return validMoves;
    }
}
