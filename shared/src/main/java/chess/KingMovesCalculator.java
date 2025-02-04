package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();


        int[] rowOption = {1,1,1,0,-1,-1,-1,0};
        int[] colOption = {-1,0,1,1,1,0,-1,-1};

        for (int i = 0; i < rowOption.length;i++){
            int newRow = rowOption[i] + currentRow;
            int newCol = colOption[i] + currentCol;

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (newPosition.getRow() > 0 && newPosition.getRow() < 9 && newPosition.getColumn() > 0 && newPosition.getColumn() < 9){
                // if new position is null add it
                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return validMoves;
    }
}
