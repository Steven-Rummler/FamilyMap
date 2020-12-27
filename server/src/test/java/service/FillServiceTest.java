package service;

import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.AuthToken;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.FillRequest;
import result.FillResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FillServiceTest {
    Database db;
    FillService fillService;
    AuthToken token;
    User user;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        db = new Database();
        Connection conn = db.getConnection();
        db.clearTables();
        UserDao uDao = new UserDao(conn);
        user = new User("samiam", "123456", "sam@gmail.com",
                "Sam", "Iam", "M", "abc123");
        uDao.insert(user);
        db.closeConnection(true);
        fillService = new FillService();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void fillPass() throws DataAccessException {
        FillRequest request = new FillRequest();
        request.user = user.getUserName();
        request.generations = 4;
        FillResult result = fillService.fill(request);
        assertTrue(result.success);
    }

    @Test
    public void fillFail() throws DataAccessException {
        FillRequest request = new FillRequest();
        request.user = "invalidusername";
        request.generations = 4;
        FillResult result = fillService.fill(request);
        assertFalse(result.success);
    }
}
