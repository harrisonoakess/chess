package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.datastorage.DBAuthDAO;
import model.*;
import org.eclipse.jetty.server.Authentication;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException, SQLException {
        if (request.username() == null || request.username().isEmpty()){
            throw new DataAccessException("Username cannot be blank");
        }
        if (request.password() == null || request.password().isEmpty()){
            throw new DataAccessException("Password cannot be blank");
        }
        if (request.email() == null || request.email().isEmpty()) {
            throw new DataAccessException("Email cannot be blank");
        }
        if (userDAO.checkUser(request.username()) == null){
            throw new DataAccessException("Error: User already exists");
        }

        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        UserData newUser = new UserData(request.username(), hashedPassword, request.email());
        userDAO.createNewUser(newUser);;

        AuthData userAuth = authDAO.createUserAuth(request.username());
//        // store the authToken here (not implemented yet)
        return new RegisterResult(request.username(), userAuth.authToken());
    }

    public LoginResult login(LoginRequest request) throws DataAccessException, SQLException {
        UserData userCheck = userDAO.checkUser(request.username());
        if (userCheck == null){
            throw new DataAccessException("User does not exists");
        }
        if (!BCrypt.checkpw(request.password(), userCheck.password())){
            throw new DataAccessException("Password does not match");
        }
        AuthData userAuth = authDAO.createUserAuth(request.username());
        return new LoginResult(request.username(), userAuth.authToken());
    }

    public void logout(String authToken) throws DataAccessException{
        if (!authDAO.checkUserAuth(authToken)){
            throw new DataAccessException("User not logged in");
        }
        authDAO.deleteUserAuth(authToken);
    }

    public void clearData() throws SQLException, DataAccessException {
        userDAO.clearUsers();
        authDAO.clearAuthTokens();
    }

}

