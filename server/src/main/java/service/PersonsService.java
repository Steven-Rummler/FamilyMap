package service;

import dao.*;
import model.AuthToken;
import model.Person;
import result.PersonsResult;

import java.sql.Connection;
import java.util.ArrayList;

public class PersonsService {
    /***
     * Returns ALL family members of the current user,
     * as determined by the current Auth Token.
     * @return PersonResult
     * @param authToken
     */
    public PersonsResult persons(AuthToken authToken) {
        PersonsResult result = new PersonsResult();
        try {
            Database database = new Database();
            database.openConnection();
            try {
                Connection conn = database.getConnection();
                PersonDao personDao = new PersonDao(conn);

                // Check if the username exists
                ArrayList<Person> persons = personDao.personsForUser(authToken.getUser());
                if (persons == null) {
                    result.message = "error: invalid person id";
                    result.success = false;
                } else {
                    result.data = new ArrayList<>();
                    result.data.addAll(persons);
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


