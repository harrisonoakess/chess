package dataaccess.datastorage;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class DBAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public AuthData createUserAuth(String username) throws DataAccessException {
        String newAuth = "INSERT INTO "
        String newAuth = java.util.UUID.randomUUID().toString();
        AuthData userAuth = new AuthData(username, newAuth);
        authTokens.put(newAuth, userAuth);
        return userAuth;
    }

    @Override
    public void deleteUserAuth(String authToken) throws DataAccessException {
        if (!checkUserAuth(authToken)) {
            throw new DataAccessException("User not authenticated");
        }
        authTokens.remove(authToken);
    }

    @Override
    public boolean checkUserAuth(String authToken) throws DataAccessException {
        return authTokens.containsKey(authToken);
    }

    @Override
    public String returnUsername(String authToken) throws DataAccessException{
        return authTokens.get(authToken).username();
    }

    @Override
    public void clearAuthTokens() {
        authTokens.clear();
    }
}
