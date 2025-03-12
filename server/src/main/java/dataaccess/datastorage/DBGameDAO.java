package dataaccess.datastorage;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.CreateGameResult;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DBGameDAO {


    public CreateGameResult createNewGame(GameData gameInfo) throws DataAccessException {
        String insertGame = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        try (
                var conn = DatabaseManager.getConnection();
                var ps = conn.prepareStatement(insertGame, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, gameInfo.whiteUsername());
            ps.setString(2, gameInfo.blackUsername());
            ps.setString(3, gameInfo.gameName());
            var jsonGame = new Gson().toJson(gameInfo.game());
            ps.setString(4, jsonGame);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int gameID = rs.getInt(1);
                    return new CreateGameResult(gameID);
                } else {
                    throw new DataAccessException("Could not find gameID");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void joinWhiteTeam(String username, Integer gameID) throws DataAccessException, SQLException {
        String checkWhiteTeam = "Select COUNT(*) FROM games WHERE gameID = ? AND whiteUsername IS NOT NULL";
        String joinWhite = "UPDATE games SET whiteUsername =? WHERE gameID = ?";
        try (
            var conn = DatabaseManager.getConnection();
            var checkPS = conn.prepareStatement(checkWhiteTeam);
            var ps = conn.prepareStatement(joinWhite)) {
            checkPS.setInt(1, gameID);
            try (var rowsAffected = checkPS.executeQuery()) {
                if (rowsAffected.next() && rowsAffected.getInt(1) > 0) {
                    throw new DataAccessException("Team already filled");
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
            ps.setString(1, username);
            ps.setInt(2, gameID);
            try {
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Failed to join white team");
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    public void joinBlackTeam(String username, Integer gameID) throws DataAccessException {
        String checkBlackTeam = "Select COUNT(*) FROM games WHERE gameID = ? AND blackUsername IS NOT NULL";
        String joinBlack = "UPDATE games SET blackUsername =? WHERE gameID = ?";
        try (
                var conn = DatabaseManager.getConnection();
                var checkPS = conn.prepareStatement(checkBlackTeam);
                var ps = conn.prepareStatement(joinBlack)) {
            checkPS.setInt(1, gameID);
            try (var rowsAffected = checkPS.executeQuery()) {
                if (rowsAffected.next() && rowsAffected.getInt(1) > 0) {
                    throw new DataAccessException("Team already filled");
                }
            } catch (SQLException e) {
                throw new DataAccessException("test" + e.getMessage());
            }
            ps.setString(1, username);
            ps.setInt(2, gameID);
            try {
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Failed to join black team");
                }
            } catch (SQLException e) {
                throw new DataAccessException("test1" + e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<Integer, GameData> listGames() throws DataAccessException, SQLException {
        Map<Integer, GameData> games = new HashMap<>();
        String getGames = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
        try (
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(getGames)){

            try (var rs = ps.executeQuery()) {
                while (rs.next()){
                    int gameID = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    String gameJson = rs.getString("game");
                    ChessGame gameConverted = new Gson().fromJson(gameJson, ChessGame.class);
                    GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, gameConverted);
                    games.put(gameID, game);
                }
                return games;
            } catch (SQLException e){
                throw new DataAccessException(e.getMessage());
            }
        }
    }
    public void clearGames() throws DataAccessException {
        String deleteGames = "DELETE FROM games";
        try (
                var conn = DatabaseManager.getConnection();
                var ps = conn.prepareStatement(deleteGames)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
