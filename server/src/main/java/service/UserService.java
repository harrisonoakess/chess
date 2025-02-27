package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.datastorage.MemoryUserDAO;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData getNewUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createNewUser(getNewUser);
        String authToken = UUID.randomUUID().toString();
        // store the authToken here (not implemented yet)
        return new RegisterResult(request.username(), authToken);
    }
}

