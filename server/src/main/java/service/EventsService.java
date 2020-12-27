package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.AuthToken;
import model.Event;
import result.EventsResult;

import java.sql.Connection;
import java.util.ArrayList;

public class EventsService {
    /***
     * Returns ALL events for ALL family members of the current user.
     * The current user is determined from the provided auth token.
     * @return EventsResult
     * @param authToken
     */
    public EventsResult events(AuthToken authToken) {
        EventsResult result = new EventsResult();
        try {
            // Connect to database
            Database database = new Database();
            database.openConnection();
            try {
                Connection conn = database.getConnection();
                EventDao eventDao = new EventDao(conn);

                // Check if the username exists
                String username = authToken.getUser();
                ArrayList<Event> events = eventDao.eventsForUser(username);
                if (events == null) {
                    result.message = "error: invalid person id";
                    result.success = false;
                } else {
                    result.data = new ArrayList<>();
                    result.data.addAll(events);
                    result.success = true;
                }

                database.closeConnection(true);
                return result;
            } catch (DataAccessException e) {
                database.closeConnection(false);
                throw e;
            }
        } catch (DataAccessException e) {
            result.message = "Data access error.";
            result.success = false;
            return result;
        }
    }
}

