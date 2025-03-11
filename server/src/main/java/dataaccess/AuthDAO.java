package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public interface AuthDAO {
    AuthData createUserAuth(String username) throws DataAccessException, SQLException;
    void deleteUserAuth(String authToken) throws DataAccessException, SQLException;
    boolean checkUserAuth(String authToken) throws DataAccessException, SQLException;
    String returnUsername(String authToken) throws DataAccessException;
    void clearAuths() throws DataAccessException;
}
