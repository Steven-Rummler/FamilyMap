package service;

import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import result.LoginResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginServiceTest {
    Database db;
    LoginService service;
    LoginRequest request;
    LoginResult result;
    User user;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        db = new Database();
        Connection conn = db.getConnection();
        db.clearTables();
        user = new User("username", "password", "email",
                "first", "last", "gender", "id");
        UserDao userDao = new UserDao(conn);
        userDao.insert(user);
        db.closeConnection(true);
        service = new LoginService();
        request = new LoginRequest();
        result = new LoginResult();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void loginPass() throws DataAccessException {
        request.userName = user.getUserName();
        request.password = user.getPassword();
        result = service.login(request);
        assertTrue(result.success);
    }

    @Test
    public void loginFail() throws DataAccessException {
        request.userName = "invalidusername";
        request.password = user.getPassword();
        result = service.login(request);
        assertFalse(result.success);

        request.userName = user.getUserName();
        request.password = "invalidpassword";
        result = service.login(request);
        assertFalse(result.success);
    }
}