package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import model.AuthData;
import model.CreateGameResult;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLGameTests {
    private DBAuthDAO authDAO;
    private DBGameDAO gameDAO;
    private GameService gameService;
    private String authToken;
    private final String username = "fake_user";

    @BeforeEach
    public void testReset() throws DataAccessException, SQLException {
        authDAO = new DBAuthDAO();
        gameDAO = new DBGameDAO();
        gameService = new GameService(gameDAO, authDAO);
        String username = "fake_user";
        String newAuth = java.util.UUID.randomUUID().toString();
        AuthData authData = authDAO.createUserAuth(username);
        authToken = authData.authToken();
        gameService.clearData();
    }
}
