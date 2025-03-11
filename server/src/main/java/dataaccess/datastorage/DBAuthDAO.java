package dataaccess.datastorage;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public AuthData createUserAuth(String username) throws DataAccessException, SQLException {
        String deleteExisting = "DELETE FROM auths WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(deleteExisting)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        String insertAuth = "INSERT INTO auths (username, authToken) VALUES (?, ?)";
        String newAuth = java.util.UUID.randomUUID().toString();
        try (
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(insertAuth)) {
                ps.setString(1, username);
                ps.setString(2, newAuth);
                ps.executeUpdate();
            } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        AuthData userAuth = new AuthData(username, newAuth);;
        return userAuth;
    }

    @Override
    public void deleteUserAuth(String authToken) throws DataAccessException, SQLException {
        String deleteAuth = "DELETE FROM auths WHERE authToken =?";
        try (
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(deleteAuth)) {
                ps.setString(1, authToken);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("User not authenticated");
                }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
            }
    }

    @Override
    public boolean checkUserAuth(String authToken) throws DataAccessException, SQLException {
        String checkForAuth = "SELECT COUNT(*) FROM auths WHERE authToken = ?";
        try (
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(checkForAuth)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                    return false;
                }
        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String returnUsername(String authToken) throws DataAccessException {
        String getUsername = "SELECT username FROM auths WHERE authToken = ?";
        try (
                var conn = DatabaseManager.getConnection();
                var ps = conn.prepareStatement(getUsername)) {
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
                throw new DataAccessException("Auth token does not exist");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void clearAuths() throws DataAccessException {
        String deleteAuths = "DELETE FROM auths";
        try (
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(deleteAuths)) {
                ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
