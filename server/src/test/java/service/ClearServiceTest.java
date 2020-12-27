package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import model.AuthToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.ClearResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearServiceTest {
    ClearService clearService;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        Database db = new Database();
        Connection conn = db.getConnection();
        db.clearTables();
        AuthTokenDao aDao = new AuthTokenDao(conn);
        AuthToken token = new AuthToken("user", "token");
        aDao.insert(token);
        db.closeConnection(true);
        clearService = new ClearService();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void clearPass() throws DataAccessException {
        ClearResult result = clearService.clear();
        assertTrue(result.success);
    }

    @Test
    public void clearPass_Multiple() throws DataAccessException {
        ClearResult result;
        for (int i = 0; i < 10; i++) {
            result = clearService.clear();
            assertTrue(result.success);
        }
    }
}
