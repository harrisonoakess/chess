package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;

public interface AuthDAO {
    void createUserAuth(AuthData auth) throws DataAccessException;
    void deleteUserAuth(AuthData auth) throws DataAccessException;
}
