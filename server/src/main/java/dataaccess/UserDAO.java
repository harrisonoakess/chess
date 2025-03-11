package dataaccess;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public interface UserDAO {
    void createNewUser(UserData user) throws DataAccessException, SQLException;
    UserData checkUser(String username) throws DataAccessException, SQLException;
    void clearUsers() throws DataAccessException, SQLException;
}
