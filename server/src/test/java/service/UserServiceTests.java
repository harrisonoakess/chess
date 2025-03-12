package service;

import dataaccess.DataAccessException;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import dataaccess.datastorage.DBUserDAO;
import model.LoginRequest;
import model.LoginResult;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserService userService;
    private DBAuthDAO authDAO;
    private DBUserDAO userDAO;

    @BeforeEach
    public void userSetup() throws SQLException, DataAccessException {
        authDAO = new DBAuthDAO();
        userDAO = new DBUserDAO(authDAO);
        userService = new UserService(userDAO, authDAO);
        userService.clearData();
    }

    @Test
    @DisplayName("Account added successfully")
    public void testRegisterSuccess() throws DataAccessException, SQLException {
        RegisterRequest request = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        RegisterResult result = userService.register(request);

        Assertions.assertEquals("fake_username", result.username());
    }
    @Test
    @DisplayName("User already exists")
    public void testUserAlreadyExists() throws DataAccessException, SQLException {
        RegisterRequest request = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(request);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
        assertEquals("Error: User already exists", exception.getMessage());
    }
    @Test
    @DisplayName("Password left blank")
    public void testBlankPassword() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("fake_username", null, "email@fake.gov");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
        assertEquals("Password cannot be blank", exception.getMessage());
    }
    @Test
    @DisplayName("Successful login")
    public void testSuccessfullyLogin() throws DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username", "fake_password");
        LoginResult loginResult = userService.login(loginRequest);

        assertEquals(loginRequest.username(), loginResult.username());
    }
    @Test
    @DisplayName("User does not exist")
    public void testUsernameNotFound() throws DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username_", "fake_password");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
        assertEquals("User does not exist", exception.getMessage());
    }
    @Test
    @DisplayName("Incorrect password")
    public void testIncorrectPassword() throws DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username", "fake_password_");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
        assertEquals("Password does not match", exception.getMessage());
    }
    @Test
    @DisplayName("successful logout")
    public void testLogout() throws DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("fake_username", "fake_password", "email@fake.gov");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("fake_username", "fake_password");
        LoginResult loginResult = userService.login(loginRequest);

        userService.logout(loginResult.authToken());
        assertFalse(authDAO.checkUserAuth(loginResult.authToken()));
    }
}

