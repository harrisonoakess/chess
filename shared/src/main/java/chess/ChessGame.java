package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor playerTurn;
    private ChessBoard board;

    public ChessGame() {
        this.playerTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();

    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return playerTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        playerTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = board.getPiece(startPosition);
        // checks if "startposition" is null
        if (currentPiece == null){
            return null;
        }
        Collection<ChessMove> calculatorMoves = currentPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : calculatorMoves) {
            ChessPiece originalPieceNewPosition = board.getPiece(move.getEndPosition());
            if (move.getPromotionPiece() != null){
                board.addPiece(move.getEndPosition(), new ChessPiece(currentPiece.getTeamColor(), move.getPromotionPiece()));
            }else{
                board.addPiece(move.getEndPosition(), currentPiece);
            }
            board.addPiece(move.getStartPosition(), null);

            if (isInCheck(currentPiece.getTeamColor()) == false){
                validMoves.add(move);
            }
            board.addPiece(move.getStartPosition(), currentPiece);
            board.addPiece(move.getEndPosition(), originalPieceNewPosition);

        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());
        // put the if statement invalid checks here.
        if (currentPiece == null){
            throw new InvalidMoveException("Current position is empty");
        }
        if (currentPiece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("It is not your turn");
        }
        if (!validMoves(move.getStartPosition()).contains(move)){
            throw new InvalidMoveException("This is not a valid move");
        }

        board.addPiece(move.getEndPosition(), currentPiece);
        board.addPiece(move.getStartPosition(), null);

        if (move.getPromotionPiece() != null){
            ChessPiece promotionPiece = new ChessPiece(currentPiece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotionPiece);
        }

        if (board.getPiece(move.getEndPosition()).getTeamColor() == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else{
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // for each of the pieces
        // for each of the valid moves for each piece
        // if one of the moves == to where the king is
        // return true
        for (int row = 1; row <= board.board.length; row++){
            for (int col = 1; col <= board.board.length; col++){
                ChessPosition piecePosition = new ChessPosition(row, col);
                if (board.getPiece(piecePosition) != null){
                    ChessPiece piece = board.getPiece(piecePosition);
                    Collection<ChessMove> moves = piece.pieceMoves(board, piecePosition);
                    for (ChessMove move : moves){
                        if (move.getEndPosition().equals(kingPosition(teamColor))){
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

    private ChessPosition kingPosition(TeamColor teamColor){
        for (int row = 1; row <= board.board.length; row++){
            for (int col = 1; col <= board.board.length; col++){
                if (board.getPiece(new ChessPosition(row, col)) != null){
                    ChessPiece kingPiece = board.getPiece(new ChessPosition(row, col));
                    if (kingPiece.getPieceType() == ChessPiece.PieceType.KING && kingPiece.getTeamColor() == teamColor){

                        return new ChessPosition(row, col);
                    }
                }
            }
        }
        return null;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor) == false){
            return false;
        }
        // if its in checkmate
        //      compare the kings valid moves to the valid moves of the enemy peices. if the king has no uniqe peices, checkmate
        for (int row = 1; row <= board.board.length; row++) {
            for (int col = 1; col <= board.board.length; col++) {
                ChessPosition piecePosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(piecePosition);

                if (board.getPiece(piecePosition) != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, piecePosition);
                    for (ChessMove move : moves) {
                        ChessPiece originalPiece = board.getPiece(move.getEndPosition());
                        board.addPiece(move.getEndPosition(), piece);
                        board.addPiece(move.getStartPosition(), null);
                        if (isInCheck(teamColor) == false){
                            board.addPiece(move.getStartPosition(), piece);
                            board.addPiece(move.getEndPosition(), originalPiece);
                            return false;
                        }
                        board.addPiece(move.getStartPosition(), piece);
                        board.addPiece(move.getEndPosition(), originalPiece);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= board.board.length; row++) {
            for (int col = 1; col <= board.board.length; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    if (moves.isEmpty() == false) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {

        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}