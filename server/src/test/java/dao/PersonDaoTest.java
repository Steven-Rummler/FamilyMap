package dao;

import model.Event;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class PersonDaoTest {
    private Database db;
    private Person bestPerson;
    private PersonDao pDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        //here we can set up any classes or variables we will need for the rest of our tests
        //lets create a new database
        db = new Database();
        //and a new Person with random data
        bestPerson = new Person("abc123", "samiam", "Sam",
                "Iam", "M", "abc123f", "abc123m",
                "abc123s");
        //Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        //Let's clear the database as well so any lingering data doesn't affect our tests
        db.clearTables();
        //Then we pass that connection to the PersonDAO so it can access the database
        pDao = new PersonDao(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        //Here we close the connection to the database file so it can be opened elsewhere.
        //We will leave commit to false because we have no need to save the changes to the database
        //between test cases
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        //While insert returns a bool we can't use that to verify that our function actually worked
        //only that it ran without causing an error
        pDao.insert(bestPerson);
        //So lets use a find method to get the Person that we just put in back out
        Person compareTest = pDao.find(bestPerson.getPersonID());
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertNotNull(compareTest);
        //Now lets make sure that what we put in is exactly the same as what we got out. If this
        //passes then we know that our insert did put something in, and that it didn't change the
        //data in any way
        assertEquals(bestPerson, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        //lets do this test again but this time lets try to make it fail
        //if we call the method the first time it will insert it successfully
        pDao.insert(bestPerson);
        //but our sql table is set up so that "PersonID" must be unique. So trying to insert it
        //again will cause the method to throw an exception
        //Note: This call uses a lambda function. What a lambda function is is beyond the scope
        //of this class. All you need to know is that this line of code runs the code that
        //comes after the "()->" and expects it to throw an instance of the class in the first parameter.
        assertThrows(DataAccessException.class, ()-> pDao.insert(bestPerson));
    }

    @Test
    public void findPass() throws DataAccessException {
        // Because the insertPass test requires both find and insert to work, we can reuse the function here.
        // There's no way to test them separately, so having both fail communicates to the programmer that
        // one might be the cause of the other.
        pDao.insert(bestPerson);
        Person compareTest = pDao.find(bestPerson.getPersonID());
        assertNotNull(compareTest);
        assertEquals(bestPerson, compareTest);
    }

    @Test
    public void findFail() throws DataAccessException {
        // Attempts to find a Person that is not in the database
        assertEquals(null, pDao.find("invalidID"));
    }

    @Test
    public void updatePass() throws DataAccessException {
        pDao.insert(bestPerson);
        String startGender = pDao.find(bestPerson.getPersonID()).getGender();
        bestPerson.setGender("F");
        pDao.update(bestPerson);
        assertNotEquals(startGender, pDao.find(bestPerson.getPersonID()).getGender());
    }

    @Test
    public void updateFail() {
        // Attempts to update a Person that is not in the database
        assertThrows(DataAccessException.class, ()-> pDao.update(bestPerson));
    }

    @Test
    public void clearUserPass() throws DataAccessException {
        pDao.insert(bestPerson);
        pDao.clearUser(bestPerson.getAssociatedUsername());
        assertEquals(null, pDao.find(bestPerson.getPersonID()));
    }

    @Test
    public void clearUserPass_MultipleUsers() throws DataAccessException {
        pDao.insert(bestPerson);
        String originalid = bestPerson.getPersonID();
        bestPerson.setPersonID("anotherid");
        pDao.insert(bestPerson);
        pDao.clearUser(bestPerson.getAssociatedUsername());
        assertEquals(null, pDao.find(originalid));
        assertEquals(null, pDao.find(bestPerson.getPersonID()));
    }

    @Test
    public void personsForUserPass() throws DataAccessException {
        pDao.insert(bestPerson);
        assertEquals(bestPerson, pDao.personsForUser(bestPerson.getAssociatedUsername()).get(0));
    }

    @Test
    public void personsForUserPass_NoPersons() throws DataAccessException {
        pDao.insert(bestPerson);
        assertEquals(0, pDao.personsForUser("fakeuser").size());
    }
}
