package dataaccess.datastorage;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class DBUserDAO implements UserDAO {

    private final DBAuthDAO authDAO;

    public DBUserDAO(DBAuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    @Override
    public void createNewUser(UserData user) throws DataAccessException, SQLException {
        String userLine = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(userLine)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                ps.setString(3, user.email());
                ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData checkUser(String username) throws DataAccessException, SQLException {
        String getUsername = "SELECT username, password, email FROM users WHERE username = ?";
        try (
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(getUsername)) {
                ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserData user = new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"));
                            return user;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    public void clearUsers() throws DataAccessException, SQLException {
        String deleteUsers = "DELETE FROM users";
        try (
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(deleteUsers)) {
                ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}


