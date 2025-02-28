package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.datastorage.DBAuthDAO;
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
        AuthData userAuth = new DBAuthDAO().createUserAuth(request.username());
        // store the authToken here (not implemented yet)
        return new RegisterResult(request.username(), userAuth.authToken());
    }

    public LoginResult login(LoginRequest request) throws DataAccessException{
        UserData loginUser = new UserData(request.username(), request.password(), null);
        userDAO.loginUser(loginUser);
        AuthData userAuth = new DBAuthDAO().createUserAuth(request.username());
        return new LoginResult(request.username(), userAuth.authToken());
    }
}

