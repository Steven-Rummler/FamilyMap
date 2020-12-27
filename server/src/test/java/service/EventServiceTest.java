package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.AuthToken;
import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.EventResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventServiceTest {
    Database db;
    EventService service;
    EventResult result;
    Event event;
    AuthToken goodToken;
    AuthToken badToken;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        db = new Database();
        Connection conn = db.getConnection();

        db.clearTables();

        event = new Event("eventid", "username", "personid",
                (float) 0, (float) 0, "country", "city", "type", 0);
        EventDao eventDao = new EventDao(conn);
        eventDao.insert(event);

        goodToken = new AuthToken("username", "token");
        badToken = new AuthToken("invalidusername", "invalidtoken");
        AuthTokenDao aDao = new AuthTokenDao(conn);
        aDao.insert(goodToken);

        db.closeConnection(true);

        service = new EventService();
        result = new EventResult();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void eventPass() throws DataAccessException {
        result = service.event(event.getEventID(), goodToken);
        assertTrue(result.success);
    }

    @Test
    public void eventFail() throws DataAccessException {
        // Invalid Auth Token
        result = service.event(event.getEventID(), badToken);
        assertFalse(result.success);
        // Nonexistent Event
        result = service.event("invalidid", goodToken);
        assertFalse(result.success);
    }
}