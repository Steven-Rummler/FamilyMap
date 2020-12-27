package service;

import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.AuthToken;
import model.Event;
import result.EventResult;

import java.sql.Connection;

public class EventService {
    /***
     * Returns the single Event object with the specified ID.
     * @param eventID the specified ID
     * @param authToken auth token for request
     * @return EventResult
     */
    public EventResult event(String eventID, AuthToken authToken) {
        EventResult result = new EventResult();
        try {
            // Connect to database
            Database database = new Database();
            database.openConnection();
            try {
                Connection conn = database.getConnection();
                EventDao eventDao = new EventDao(conn);

                // Check if the event exists
                Event event = eventDao.find(eventID);
                if (event == null) {
                    result.message = "error: invalid event id";
                    result.success = false;
                } else if (!event.getAssociatedUsername().equals(authToken.getUser())) {
                    result.message = "error: requested event does not belong to this user";
                    result.success = false;
                }  else {
                    result.eventID = event.getEventID();
                    result.associatedUsername = event.getAssociatedUsername();
                    result.personID = event.getPersonID();
                    result.latitude = event.getLatitude();
                    result.longitude = event.getLongitude();
                    result.country = event.getCountry();
                    result.city = event.getCity();
                    result.eventType = event.getEventType();
                    result.year = event.getYear();
                    result.success = true;
                }

                database.closeConnection(true);
            } catch (DataAccessException e) {
                database.closeConnection(false);
                throw e;
            }

            return result;
        } catch (DataAccessException e) {
            result.message = "Data access error.";
            result.success = false;
            return result;
        }
    }
}
