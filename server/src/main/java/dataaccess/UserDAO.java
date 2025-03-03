package dataaccess;
import model.UserData;

public interface UserDAO {
    void createNewUser(UserData user) throws DataAccessException;
    void loginUser(UserData user) throws DataAccessException;
    void logoutUser(String authToken) throws DataAccessException;
    void clearUsers();
}
