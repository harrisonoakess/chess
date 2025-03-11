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
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}


//    @Override
//    public void loginUser(UserData user) throws DataAccessException {
//        if (!users.containsKey(user.username())){
//            throw new DataAccessException("User does not exists");
//        }
//        if (!Objects.equals(user.password(), users.get(user.username()).password())){
//            throw new DataAccessException("Password does not match");
//        }
//        String token = java.util.UUID.randomUUID().toString();
//        new AuthData(token, users.get(user.username()).username());
//    }
//
//    @Override
//    public void logoutUser(String authToken) throws DataAccessException {
//        if (!authDAO.checkUserAuth(authToken)){
//            throw new DataAccessException("User not logged in");
//        }
//        authDAO.deleteUserAuth(authToken);
//    }
//
//    public void clearUsers(){
//        users.clear();
//    }
//}
//

