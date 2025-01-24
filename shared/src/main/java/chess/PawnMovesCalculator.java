package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Gets the position of the current piece
        int position_row = myPosition.getRow();
        int position_col = myPosition.getColumn();

        ChessGame.TeamColor teamColor = board.getPiece(myPosition).getTeamColor();

        if (teamColor == ChessGame.TeamColor.WHITE) {
            ChessPosition upOne = new ChessPosition(position_row + 2, position_col + 1);
            if (upOne.getRow() < 8 && upOne.getColumn() < 8 && upOne.getRow() >= 0 && upOne.getColumn() >= 0) {
                if (board.getPiece(upOne) == null && upOne.getRow() == 7) {
                    validMoves.add(new ChessMove(myPosition, upOne, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, upOne, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, upOne, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, upOne, ChessPiece.PieceType.KNIGHT));
                } else if (board.getPiece(upOne) == null) {
                    validMoves.add(new ChessMove(myPosition, upOne, null));

                    if (position_row == 1) {
                        ChessPosition upTwo = new ChessPosition(position_row + 3, position_col + 1);
                        if (board.getPiece(upTwo) == null) {
                            validMoves.add(new ChessMove(myPosition, upTwo, null));
                        }
                    }
                }
            }
            // Capture Diagonally left
            ChessPosition captureLeft = new ChessPosition(position_row + 2, position_col);
            if (captureLeft.getRow() < 8 && captureLeft.getRow() >= 0 && captureLeft.getColumn() >= 0 && captureLeft.getColumn() < 8) {
                if (board.getPiece(captureLeft) != null && board.getPiece(captureLeft).getTeamColor() != teamColor) {
                    if (captureLeft.getRow() == 7){
                        validMoves.add(new ChessMove(myPosition, captureLeft, ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, captureLeft, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, captureLeft, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, captureLeft, ChessPiece.PieceType.KNIGHT));
                    } else{
                        validMoves.add(new ChessMove(myPosition, captureLeft, null));
                    }

                }
            }

            // Capture diagonally right
            ChessPosition captureRight = new ChessPosition(position_row + 2, position_col + 2);
            if (captureRight.getRow() < 8 && captureRight.getRow() >= 0 && captureRight.getColumn() >= 0 && captureRight.getColumn() < 8) {
                if (board.getPiece(captureRight) != null && board.getPiece(captureRight).getTeamColor() != teamColor) {
                    if (captureRight.getRow() == 7){
                        validMoves.add(new ChessMove(myPosition, captureRight, ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, captureRight, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, captureRight, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, captureRight, ChessPiece.PieceType.KNIGHT));
                    } else{
                        validMoves.add(new ChessMove(myPosition, captureRight, null));
                    }

                }
            }


        } else {
            ChessPosition downOne = new ChessPosition(position_row, position_col + 1);
            if (downOne.getRow() < 8 && downOne.getColumn() < 8 && downOne.getRow() >= 0 && downOne.getColumn() >= 0) {
                if (board.getPiece(downOne) == null && downOne.getRow() == 0) {
                    validMoves.add(new ChessMove(myPosition, downOne, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, downOne, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, downOne, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, downOne, ChessPiece.PieceType.KNIGHT));
                } else if (board.getPiece(downOne) == null) {
                    validMoves.add(new ChessMove(myPosition, downOne, null));

                    if (position_row == 6) {
                        ChessPosition downTwo = new ChessPosition(position_row - 1, position_col + 1);
                        if (board.getPiece(downTwo) == null) {
                            validMoves.add(new ChessMove(myPosition, downTwo, null));
                        }
                    }
                }
            }
            // Capture Diagonally left
            ChessPosition captureDownLeft = new ChessPosition(position_row, position_col + 2);
            if (captureDownLeft.getRow() < 8 && captureDownLeft.getRow() >= 0 && captureDownLeft.getColumn() >= 0 && captureDownLeft.getColumn() < 8) {
                if (board.getPiece(captureDownLeft) != null && board.getPiece(captureDownLeft).getTeamColor() != teamColor) {
                    if (captureDownLeft.getRow() == 0){
                        validMoves.add(new ChessMove(myPosition, captureDownLeft, ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, captureDownLeft, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, captureDownLeft, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, captureDownLeft, ChessPiece.PieceType.KNIGHT));
                    } else{
                        validMoves.add(new ChessMove(myPosition, captureDownLeft, null));
                    }

                }
            }

            // Capture diagonally right
            ChessPosition captureDownRight = new ChessPosition(position_row, position_col);
            if (captureDownRight.getRow() < 8 && captureDownRight.getRow() >= 0 && captureDownRight.getColumn() >= 0 && captureDownRight.getColumn() < 8) {
                if (board.getPiece(captureDownRight) != null && board.getPiece(captureDownRight).getTeamColor() != teamColor) {
                    if (captureDownRight.getRow() == 0){
                        validMoves.add(new ChessMove(myPosition, captureDownRight, ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, captureDownRight, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, captureDownRight, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, captureDownRight, ChessPiece.PieceType.KNIGHT));
                    } else{
                        validMoves.add(new ChessMove(myPosition, captureDownRight, null));
                    }

                }
            }


        }
        return validMoves;
    }
}











//            if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
//
//            // white moves
//            int[] optionalRowMovesWhite = {0, 1, -1};
//            int[] optionalColMovesWhite = {2, 1, 1};
//
//            // takes into account where the starting position of the piece is
//            for (int i = 0; i < optionalRowMovesWhite.length; i++) {
//                int positionalRowMoves = (position_row + optionalRowMovesWhite[i]) + 1;
//                int positionalColMoves = (position_col + optionalColMovesWhite[i]) + 1;
//
//                // creates a new position bases off optional moves
//                ChessPosition pieceNewPosition = new ChessPosition(positionalRowMoves, positionalColMoves);
//
//                // checks to see if it's on the board
//                if (pieceNewPosition.getRow() < 8 && pieceNewPosition.getColumn() < 8 && pieceNewPosition.getRow() >= 0 && pieceNewPosition.getColumn() >= 0) {
//                    if (myPosition.getRow() == 2 && board.getPiece(pieceNewPosition) == null && new ChessPosition(positionalRowMoves, positionalColMoves + 1) == null) {
//                        validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//
//                    } else if (myPosition.getColumn() != positionalRowMoves && board.getPiece(pieceNewPosition) != null && board.getPiece(pieceNewPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
//                        validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//                    } else if (board.getPiece(pieceNewPosition) == null){
//                        validMoves.add(new ChessMove(myPosition, pieceNewPosition, null));
//                    }
//                }
//            }
//        } else {
//
//        }
//        return validMoves;
//    }
//}
