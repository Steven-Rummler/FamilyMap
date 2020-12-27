package service;

import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import dao.PersonDao;
import dao.UserDao;
import model.Event;
import model.Person;
import model.User;
import request.LoadRequest;
import result.LoadResult;

import java.sql.Connection;

public class LoadService {
    /**
     * Clears all data from the database (just like the /clear API),
     * and then loads the posted user, person, and event data into
     * the database.
     *
     * @param r LoadRequest
     * @return LoadResult
     */
    public LoadResult load(LoadRequest r) {
        LoadResult result = new LoadResult();
        try {
            Database database = new Database();
            database.openConnection();
            Connection conn = database.getConnection();
            UserDao userDao = new UserDao(conn);
            PersonDao personDao = new PersonDao(conn);
            EventDao eventDao = new EventDao(conn);

            try {
                ClearService clearService = new ClearService();
                clearService.clear();

                for (Person person : r.persons) {
                    if (person.getAssociatedUsername() == null) person.setAssociatedUsername("");
                    personDao.insert(person);
                }
                for (User user : r.users) {
                    userDao.insert(user);
                }
                for (Event event : r.events) {
                    if (event.getAssociatedUsername() == null) event.setAssociatedUsername("");
                    eventDao.insert(event);
                }
                database.closeConnection(true);

                result.message = "Successfully added " + r.users.size() + " users, " + r.persons.size() + " persons, and " +
                        r.events.size() + " events to the database.";
                result.success = true;
                return result;
            } catch (DataAccessException e) {
                database.closeConnection(false);
                throw e;
            }
        } catch (DataAccessException e) {
            result.message = "database error";
            result.success = false;
            return result;
        }
    }
}
