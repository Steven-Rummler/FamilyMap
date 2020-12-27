package service;

import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import model.AuthToken;
import model.Person;
import result.PersonResult;

import java.sql.Connection;

public class PersonService {
    /***
     * Returns the single Person object with the specified ID.
     *
     * @param personID specified ID
     * @return PersonResult
     */
    public PersonResult person(String personID, AuthToken authToken) {
        PersonResult result = new PersonResult();
        try {
            // Connect to database
            Database database = new Database();
            database.openConnection();
            try {
                Connection conn = database.getConnection();
                PersonDao personDao = new PersonDao(conn);

                // Check if the username exists
                Person person = personDao.find(personID);
                if (person == null) {
                    result.message = "error: invalid person id";
                    result.success = false;
                } else if (!person.getAssociatedUsername().equals(authToken.getUser())) {
                    result.message = "error: requested person does not belong to this user";
                    result.success = false;
                } else {
                    result.personID = person.getPersonID();
                    result.associatedUsername = person.getAssociatedUsername();
                    result.firstName = person.getFirstName();
                    result.lastName = person.getLastName();
                    result.gender = person.getGender();
                    result.fatherID = person.getFatherID();
                    result.motherID = person.getMotherID();
                    result.spouseID = person.getSpouseID();
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

