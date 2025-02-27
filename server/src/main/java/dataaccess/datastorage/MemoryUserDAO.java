package dataaccess.datastorage;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createNewUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())){
            throw new DataAccessException("Error: already taken");
        }if (Objects.equals(user.password(), null)){
            throw new DataAccessException("Password cannot be blank");
        }else {
            // puts in the user: key=username, value=user (record)
            users.put(user.username(), user);
        }
    }

    @Override
    public AuthData loginUser(UserData user) throws DataAccessException {
        if (!users.containsKey(user.username())){
            throw new DataAccessException("User does not exists");
        }
        if (!Objects.equals(user.password(), users.get(user.username()).password())){
            throw new DataAccessException("Password does not match");
        }
        String token = java.util.UUID.randomUUID().toString();
        return new AuthData(token, users.get(user.username()).username());
    }

    @Override
    public void logoutUser(UserData user, AuthData auth) {
    }

    public void ClearUsers(){
        users.clear();
    }
}


