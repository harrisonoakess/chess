package dataaccess;
import model.AuthData;
import model.UserData;

public interface UserDAO {
    void createNewUser(UserData user) throws DataAccessException;
    void loginUser(UserData user) throws DataAccessException;
    void logoutUser(UserData user, AuthData auth);
    void ClearUsers();
}
