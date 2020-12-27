package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import model.AuthToken;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.PersonResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PersonServiceTest {
    Database db;
    PersonService service;
    PersonResult result;
    Person person;
    AuthToken goodToken;
    AuthToken badToken;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        db = new Database();
        Connection conn = db.getConnection();

        db.clearTables();

        person = new Person("personid", "username", "first",
                "last", "gender", "fatherid", "motherid", "spouseid");
        PersonDao personDao = new PersonDao(conn);
        personDao.insert(person);

        goodToken = new AuthToken("username", "token");
        badToken = new AuthToken("invalidusername", "invalidtoken");
        AuthTokenDao aDao = new AuthTokenDao(conn);
        aDao.insert(goodToken);

        db.closeConnection(true);

        service = new PersonService();
        result = new PersonResult();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void personPass() throws DataAccessException {
        result = service.person(person.getPersonID(), goodToken);
        assertTrue(result.success);
    }

    @Test
    public void personFail() throws DataAccessException {
        // Invalid Auth Token
        result = service.person(person.getPersonID(), badToken);
        assertFalse(result.success);
        // Nonexistent Person
        result = service.person("invalidid", goodToken);
        assertFalse(result.success);
    }
}