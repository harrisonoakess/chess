package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createUserAuth(String username) throws DataAccessException;
    void deleteUserAuth(String username) throws DataAccessException;
    boolean checkUserAuth(String username) throws DataAccessException;
    void ClearDatabase();
}
