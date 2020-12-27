package service;

import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import model.AuthToken;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.PersonsResult;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PersonsServiceTest {
    Database db;
    PersonsService service;
    PersonsResult result;
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

        db.closeConnection(true);

        service = new PersonsService();
        result = new PersonsResult();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void personsPass() throws DataAccessException {
        result = service.persons(goodToken);
        assertTrue(result.success);
        assertEquals(1, result.data.size());
    }

    @Test
    public void personsFail() throws DataAccessException {
        // Invalid Auth Token
        result = service.persons(badToken);
        assertEquals(0, result.data.size());
    }
}