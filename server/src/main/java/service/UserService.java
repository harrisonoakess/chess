package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.*;

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

    public LoginResult login(LoginRequest request) throws DataAccessException{
        UserData loginUser = new UserData(request.username(), request.username(), null);
        userDAO.loginUser(loginUser);
        String authToken = UUID.randomUUID().toString();
        return new LoginResult(request.username(), authToken);
    }
}

