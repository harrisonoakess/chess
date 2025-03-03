package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createUserAuth(String username) throws DataAccessException;
    void deleteUserAuth(String username) throws DataAccessException;
    boolean checkUserAuth(String username) throws DataAccessException;

    String returnUsername(String authToken) throws DataAccessException;

    void clearAuthTokens();
}
