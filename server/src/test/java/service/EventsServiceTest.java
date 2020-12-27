package service;

import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.AuthToken;
import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.EventsResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventsServiceTest {
    Database db;
    EventsService service;
    EventsResult result;
    Event event;
    AuthToken goodToken;
    AuthToken badToken;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        db = new Database();
        Connection conn = db.getConnection();

        db.clearTables();

        event = new Event("eventsid", "username", "personid",
                (float) 0, (float) 0, "country", "city", "type", 0);
        EventDao eventDao = new EventDao(conn);
        eventDao.insert(event);

        goodToken = new AuthToken("username", "token");
        badToken = new AuthToken("invalidusername", "invalidtoken");

        db.closeConnection(true);

        service = new EventsService();
        result = new EventsResult();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void eventsPass() throws DataAccessException {
        result = service.events(goodToken);
        assertTrue(result.success);
        assertEquals(1, result.data.size());
    }

    @Test
    public void eventsFail() throws DataAccessException {
        // Invalid Auth Token
        result = service.events(badToken);
        assertEquals(0, result.data.size());
    }
}