package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor piece_color;
    private final ChessPiece.PieceType piece_type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return piece_color == that.piece_color && piece_type == that.piece_type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece_color, piece_type);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.piece_color = pieceColor;
        this.piece_type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return piece_color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return piece_type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator calculator = null;
        if (piece_type == PieceType.KING){
            calculator = new KingMovesCalculator();
        }
        else if (piece_type == PieceType.QUEEN){
            calculator = new QueenMovesCalculator();
        }
        else if (piece_type == PieceType.BISHOP){
            calculator = new BishopMovesCalculator();
        }
        else if (piece_type == PieceType.KNIGHT){
            calculator = new KnightMovesCalculator();
        }
        else if (piece_type == PieceType.ROOK){
            calculator = new RookMovesCalculator();
        }
        else if (piece_type == PieceType.PAWN){
            calculator = new PawnMovesCalculator();
        }


        return calculator.pieceMoves(board, myPosition);
    }
}
