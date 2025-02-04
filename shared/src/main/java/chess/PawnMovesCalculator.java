package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            // if front clear
            if (myPosition.getRow() + 1 > 0 && myPosition.getRow() + 1 < 9 && myPosition.getColumn() > 0 && myPosition.getColumn() < 9) {
                ChessPosition newPositionWhite = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                if (board.getPiece(newPositionWhite) == null) {
                    if (myPosition.getRow() == 7) {
                        validMoves.add(new ChessMove(myPosition, newPositionWhite, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, newPositionWhite, ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, newPositionWhite, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, newPositionWhite, ChessPiece.PieceType.QUEEN));
                    } else if (myPosition.getRow() == 2) {
                        validMoves.add(new ChessMove(myPosition, newPositionWhite, null));
                        ChessPosition newPositionPlusOne = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                        if (board.getPiece(newPositionPlusOne) == null) {
                            validMoves.add(new ChessMove(myPosition, newPositionPlusOne, null));
                        }

                    } else {
                        validMoves.add(new ChessMove(myPosition, newPositionWhite, null));
                    }
                }
            }
            // up and left
            if (myPosition.getRow() + 1 > 0 && myPosition.getRow() + 1 < 9 && myPosition.getColumn()-1 > 0 && myPosition.getColumn()-1 < 9) {
                ChessPosition upLeft = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
                if (board.getPiece(upLeft) != null && board.getPiece(myPosition).getTeamColor() != board.getPiece(upLeft).getTeamColor()){
                    if(myPosition.getRow() == 7){
                        validMoves.add(new ChessMove(myPosition, upLeft, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, upLeft, ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, upLeft, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, upLeft, ChessPiece.PieceType.QUEEN));
                    }else{
                        validMoves.add(new ChessMove(myPosition, upLeft, null));
                    }
                }
            }
            // up and right
            if (myPosition.getRow() + 1 > 0 && myPosition.getRow() + 1 < 9 && myPosition.getColumn() + 1 > 0 && myPosition.getColumn() + 1 < 9) {
                ChessPosition upRight = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
                if (board.getPiece(upRight) != null && board.getPiece(myPosition).getTeamColor() != board.getPiece(upRight).getTeamColor()){
                    if(myPosition.getRow() == 7){
                        validMoves.add(new ChessMove(myPosition, upRight, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, upRight, ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, upRight, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, upRight, ChessPiece.PieceType.QUEEN));
                    }else{
                        validMoves.add(new ChessMove(myPosition, upRight, null));
                    }
                }
            }




        }else{ChessPosition newPositionBlack = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            if (myPosition.getRow() + 1 > 0 && myPosition.getRow() + 1 < 9 && myPosition.getColumn() > 0 && myPosition.getColumn() < 9) {
                if (board.getPiece(newPositionBlack) == null) {
                    if (myPosition.getRow() == 2) {
                        validMoves.add(new ChessMove(myPosition, newPositionBlack, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, newPositionBlack, ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, newPositionBlack, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, newPositionBlack, ChessPiece.PieceType.QUEEN));
                    } else if (myPosition.getRow() == 7) {
                        validMoves.add(new ChessMove(myPosition, newPositionBlack, null));
                        ChessPosition newPositionMinusOne = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                        if (board.getPiece(newPositionMinusOne) == null) {
                            validMoves.add(new ChessMove(myPosition, newPositionMinusOne, null));
                        }
                    }else{
                        validMoves.add(new ChessMove(myPosition, newPositionBlack, null));

                    }
                }
            }
            // down and left
            if (myPosition.getRow() - 1 > 0 && myPosition.getRow() - 1 < 9 && myPosition.getColumn()-1 > 0 && myPosition.getColumn()-1 < 9) {
                ChessPosition downLeft = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
                if (board.getPiece(downLeft) != null && board.getPiece(myPosition).getTeamColor() != board.getPiece(downLeft).getTeamColor()){
                    if(myPosition.getRow() == 2){
                        validMoves.add(new ChessMove(myPosition, downLeft, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, downLeft, ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, downLeft, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, downLeft, ChessPiece.PieceType.QUEEN));
                    }else{
                        validMoves.add(new ChessMove(myPosition, downLeft, null));
                    }
                }
            }
            // up and right
            if (myPosition.getRow() - 1 > 0 && myPosition.getRow() - 1 < 9 && myPosition.getColumn() + 1 > 0 && myPosition.getColumn() + 1 < 9) {
                ChessPosition downRight = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
                if (board.getPiece(downRight) != null && board.getPiece(myPosition).getTeamColor() != board.getPiece(downRight).getTeamColor()){
                    if(myPosition.getRow() == 2){
                        validMoves.add(new ChessMove(myPosition, downRight, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, downRight, ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(myPosition, downRight, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, downRight, ChessPiece.PieceType.QUEEN));
                    }else{
                        validMoves.add(new ChessMove(myPosition, downRight, null));
                    }
                }
            }
        }
        return validMoves;
    }
}
