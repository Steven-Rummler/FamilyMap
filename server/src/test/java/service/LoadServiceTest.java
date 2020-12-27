package service;

import dao.DataAccessException;
import dao.Database;
import model.Event;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoadRequest;
import result.LoadResult;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadServiceTest {
    Database db;
    LoadService loadService;
    LoadRequest request;
    LoadRequest badrequest;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        db = new Database();
        Connection conn = db.getConnection();
        db.clearTables();
        db.closeConnection(true);
        loadService = new LoadService();
        request = new LoadRequest();
        request.users = new ArrayList<>();

        request.persons = new ArrayList<>();
        request.events = new ArrayList<>();
        request.events.add(new Event("eventsid", "username", "personid",
                (float) 0, (float) 0, "country", "city", "type", 0));
        badrequest = new LoadRequest();
        badrequest.users = new ArrayList<>();
        badrequest.persons = new ArrayList<>();
        badrequest.events = new ArrayList<>();
        badrequest.events.add(new Event("eventsid", "username", "personid",
                (float) 0, (float) 0, "country", "city", "type", 0));
        badrequest.events.add(new Event("eventsid", "username", "personid",
                (float) 0, (float) 0, "country", "city", "type", 0));
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void loadPass() throws DataAccessException {
        LoadResult result = loadService.load(request);
        assertTrue(result.success);
    }

    @Test
    public void loadFail() throws DataAccessException {
        LoadResult result = loadService.load(badrequest);
        assertFalse(result.success);
    }
}