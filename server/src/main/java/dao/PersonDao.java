package dao;

import helper.StringJsonHelper;
import model.Event;
import model.Person;
import model.User;

import java.sql.*;

import java.util.ArrayList;

public class PersonDao {
    private final Connection conn;

    /**
     * Connects to the database
     *
     * @param conn Database Connection Object
     */
    public PersonDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts the given person into the database
     * @param person
     */
    public void insert(Person person) throws DataAccessException {
        String sql = "INSERT INTO Persons (PersonID, AssociatedUsername, FirstName, LastName, Gender, FatherID, MotherID, SpouseID) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * Updates the given person into the database
     * @param person the person to update
     */
    public void update(Person person) throws DataAccessException {
        if (find(person.getPersonID()) == null) {
            throw new DataAccessException("Error encountered while updating the database");
        }
        String sql = "UPDATE Persons SET AssociatedUsername = ?, FirstName = ?, LastName = ?," +
                "Gender = ?, FatherID = ?, MotherID = ?, SpouseID = ? WHERE PersonID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, person.getAssociatedUsername());
            stmt.setString(2, person.getFirstName());
            stmt.setString(3, person.getLastName());
            stmt.setString(4, person.getGender());
            stmt.setString(5, person.getFatherID());
            stmt.setString(6, person.getMotherID());
            stmt.setString(7, person.getSpouseID());
            stmt.setString(8, person.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while updating the database");
        }
    }

    /**
     * Returns the person with the given personID
     *
     * @param personID
     * @return the person if found
     */
    public Person find(String personID) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE PersonID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("FatherID"),
                        rs.getString("MotherID"), rs.getString("SpouseID"));
                return person;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * Deletes all persons in the database associated with a specific user
     * @param username the user to clear
     */
    public void clearUser(String username) throws DataAccessException {
        String sql = "DELETE FROM Persons WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing persons for user");
        }
    }

    /**
     * Returns all person objects associated with the given user
     * @param username
     * @return persons for user
     */
    public ArrayList<Person> personsForUser(String username) throws DataAccessException {
        ArrayList<Person> persons = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Person person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("FatherID"),
                        rs.getString("MotherID"), rs.getString("SpouseID"));
                persons.add(person);
            }
            return persons;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding persons for user");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Generates a random id string and confirms that it is not in use
     * @return A random id string
     * @throws DataAccessException
     */
    public String generateID() throws DataAccessException {
        boolean generated = false;
        int tries = 0;
        String id = "";
        while (!generated && tries < 20) {
            tries++;
            id = StringJsonHelper.getRandomString(16);
            if (find(id) == null) {
                generated = true;
            }
        }
        if (tries < 20) {
            return id;
        }
        return "";
    }
}
