package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createUserAuth(String username) throws DataAccessException;
    void deleteUserAuth(UserData user) throws DataAccessException;
    boolean checkUserAuth(UserData user) throws DataAccessException;
    void ClearDatabase();
}
