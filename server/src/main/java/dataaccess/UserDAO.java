package dataaccess;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

public interface UserDAO {
    void createNewUser(UserData user) throws DataAccessException;
    AuthData loginUser(UserData user) throws DataAccessException;
    void logoutUser(UserData user, AuthData auth);
    void ClearUsers();
}
