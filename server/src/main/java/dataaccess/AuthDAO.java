package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createUserAuth(AuthData auth) throws DataAccessException;
    void deleteUserAuth(AuthData auth) throws DataAccessException;
    void ClearDatabase();
}
