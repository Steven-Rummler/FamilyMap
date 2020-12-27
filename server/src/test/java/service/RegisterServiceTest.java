package service;

import dao.DataAccessException;
import dao.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterServiceTest {
    Database db;
    RegisterService registerService;
    RegisterRequest request;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        db = new Database();
        Connection conn = db.getConnection();
        db.clearTables();
        db.closeConnection(true);
        registerService = new RegisterService();
        request = new RegisterRequest();
        request.userName = "username";
        request.email = "email";
        request.password = "password";
        request.firstName = "first";
        request.lastName = "last";
        request.gender = "gender";
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void registerPass() throws DataAccessException {
        RegisterResult result = registerService.register(request);
        assertTrue(result.success);
    }

    @Test
    public void registerFail() throws DataAccessException {
        RegisterResult result = registerService.register(request);
        assertTrue(result.success);
        result = registerService.register(request);
        assertFalse(result.success);
    }
}
