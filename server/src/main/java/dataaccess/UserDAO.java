package dataaccess;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;

public interface UserDAO {
    void createNewUser(UserData user) throws DataAccessException;
    void loginUser(UserData user) throws DataAccessException;
    void logoutUser(String authToken) throws DataAccessException;
    void ClearUsers();
}
