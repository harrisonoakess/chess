package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.datastorage.DBAuthDAO;
import model.*;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData getNewUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createNewUser(getNewUser);
        AuthData userAuth = authDAO.createUserAuth(request.username());
        // store the authToken here (not implemented yet)
        return new RegisterResult(request.username(), userAuth.authToken());
    }

    public LoginResult login(LoginRequest request) throws DataAccessException{
        UserData loginUser = new UserData(request.username(), request.password(), null);
        userDAO.loginUser(loginUser);
        AuthData userAuth = authDAO.createUserAuth(request.username());
        return new LoginResult(request.username(), userAuth.authToken());
    }

    public void logout(String authToken) throws DataAccessException{
        userDAO.logoutUser(authToken);
    }

}

