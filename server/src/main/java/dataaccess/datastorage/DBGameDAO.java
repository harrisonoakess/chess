package dataaccess.datastorage;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.CreateGameResult;
import model.GameData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBGameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private final DBAuthDAO authDAO;
    private int nextGameID = 1;

    public DBGameDAO(DBAuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public CreateGameResult createNewGame(GameData gameInfo) throws DataAccessException {
        String insertGame = "INSERT INTO games (whiteUsername, blackUsername, gameName, gameState) VALUES (?, ?, ?, ?)";
        try (
                var conn = DatabaseManager.getConnection();
                var ps = conn.prepareStatement(insertGame)) {
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
        String joinWhite = "UPDATE games SET whiteUsername =?";
        try (
            var conn = DatabaseManager.getConnection();
            var checkPS = conn.prepareStatement(checkWhiteTeam);
            var ps = conn.prepareStatement(joinWhite)) {
            checkPS.setInt(1, gameID);
            try (var rowsAffected = checkPS.executeQuery()) {
                if (rowsAffected.next() && rowsAffected.getInt(1) > 0) {
                    throw new DataAccessException("White team already filled");
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
        String joinBlack = "UPDATE games SET blackUsername =?";
        try (
                var conn = DatabaseManager.getConnection();
                var checkPS = conn.prepareStatement(checkBlackTeam);
                var ps = conn.prepareStatement(joinBlack)) {
            checkPS.setInt(1, gameID);
            try (var rowsAffected = checkPS.executeQuery()) {
                if (rowsAffected.next() && rowsAffected.getInt(1) > 0) {
                    throw new DataAccessException("Black team already filled");
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
            ps.setString(1, username);
            ps.setInt(2, gameID);
            try {
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Failed to join black team");
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, GameData> listGames() throws DataAccessException {
        return new HashMap<>(games);
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
