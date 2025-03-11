package dataaccess;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public interface UserDAO {
    void createNewUser(UserData user) throws DataAccessException, SQLException;
    void loginUser(UserData user) throws DataAccessException;
    void logoutUser(String authToken) throws DataAccessException;
    UserData checkUser(String username) throws DataAccessException;
    void clearUsers();
}
