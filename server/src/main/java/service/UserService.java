package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.datastorage.MemoryUserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDAO = new MemoryUserDAO();

    public LoginHelper login(String username, String password){
        try {
            UserData user = new UserData(username, password, "");
            AuthData auth = userDAO.loginUser(user);
            return new LoginHelper(auth.username(), auth.authToken(), 200, null);
        } catch (DataAccessException exception){
            return new LoginHelper(null, null, 401, exception.getMessage());
        }

        }
}
