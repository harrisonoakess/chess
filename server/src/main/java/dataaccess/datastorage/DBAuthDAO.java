package dataaccess.datastorage;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class DBAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public AuthData createUserAuth(String username) throws DataAccessException {
        String newAuth = java.util.UUID.randomUUID().toString();
        AuthData userAuth = new AuthData(username, newAuth);
        authTokens.put(username, userAuth);
        return userAuth;
    }

    @Override
    public void deleteUserAuth(UserData user) throws DataAccessException {
        if (!checkUserAuth(user)) {
            throw new DataAccessException("User not authenticated");
        }
        authTokens.remove(user.username());
    }

    @Override
    public boolean checkUserAuth(UserData user) throws DataAccessException {
        return authTokens.containsKey(user.username());
    }

    @Override
    public void ClearDatabase() {
        authTokens.clear();
    }
}
