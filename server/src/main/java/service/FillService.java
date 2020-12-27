package service;

import com.google.gson.Gson;
import dao.*;
import model.Event;
import model.Person;
import model.User;
import request.FillRequest;
import result.FillResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;

public class FillService {
    /**
     * Populates the server's database with generated data for
     * the specified user name. The required "username" parameter
     * must be a user already registered with the server.
     *
     * If there is any data in the database already associated
     * with the given user name, it is deleted.
     *
     * The optional "generations" parameter lets the caller specify
     * the number of generations of ancestors to be generated, and
     * must be a non-negative integer (the default is 4, which
     * results in 31 new persons each with associated events).
     *
     * @param r FillRequest
     * @return FillResult
     */
    public FillResult fill(FillRequest r) {
        FillResult result = new FillResult();
        try {
            // Connect to database
            Database database = new Database();
            database.openConnection();
            try {
                Connection conn = database.getConnection();
                UserDao userDao = new UserDao(conn);
                PersonDao personDao = new PersonDao(conn);
                EventDao eventDao = new EventDao(conn);

                String username = r.user;
                Integer generations = r.generations;

                User user = userDao.find(username);

                if (user != null) {
                    // Delete persons and events associated with the username
                    personDao.clearUser(username);
                    eventDao.clearUser(username);

                    // Create a person for the user
                    Person userPerson = new Person(user.getPersonID(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getGender(), "", "", "");
                    personDao.insert(userPerson);
                    int startYear = 1980;
                    int birthYear = (int) (startYear + Math.round(5 * Math.random()));
                    Location birthLocation = getLocation();
                    Event userBirth = new Event(eventDao.generateID(), username, userPerson.getPersonID(), birthLocation.latitude, birthLocation.longitude, birthLocation.country, birthLocation.city, "birth", birthYear);
                    eventDao.insert(userBirth);

                    ArrayList<Person> currentGeneration = new ArrayList<>();
                    currentGeneration.add(userPerson);

                    // Fill out all generations
                    // Each generation is an array.  Each person in the array gets parents, which are added to a new array.  Then the process starts over with the array
                    int personsAdded = 1;
                    for (int i = 1; i <= generations; i++) {
                        int generationYear = startYear - 30 * i;
                        ArrayList<Person> previousGeneration = new ArrayList<>();
                        for (Person person : currentGeneration) {
                            // Generate parents
                            Person father = generatePerson(username, generationYear, true, personDao, eventDao);
                            Person mother = generatePerson(username, generationYear, false, personDao, eventDao);

                            // Marry them
                            int marriageYear = (int) (generationYear + 21 + Math.round(4 * Math.random()));
                            Location marriagePlace = getLocation();
                            eventDao.insert(new Event(eventDao.generateID(), username, father.getPersonID(), marriagePlace.latitude, marriagePlace.longitude, marriagePlace.country, marriagePlace.city, "marriage", marriageYear));
                            eventDao.insert(new Event(eventDao.generateID(), username, mother.getPersonID(), marriagePlace.latitude, marriagePlace.longitude, marriagePlace.country, marriagePlace.city, "marriage", marriageYear));
                            father.setSpouseID(mother.getPersonID());
                            mother.setSpouseID(father.getPersonID());

                            // Attach them to the child and insert into the database
                            person.setFatherID(father.getPersonID());
                            person.setMotherID(mother.getPersonID());
                            personDao.insert(father);
                            personDao.insert(mother);
                            personDao.update(person);
                            previousGeneration.add(father);
                            previousGeneration.add(mother);
                            personsAdded += 2;
                        }
                        currentGeneration = previousGeneration;
                    }
                    result.message = "Successfully added " + personsAdded + " persons and " +
                            (3 * personsAdded) + " events to the database";
                    result.success = true;
                } else {
                    result.message = "Invalid username.";
                    result.success = false;
                }

                database.closeConnection(true);
            } catch (DataAccessException | IOException e) {
                database.closeConnection(false);
                throw e;
            }

            return result;
        } catch (DataAccessException e) {
            result.message = "Data access error.";
            result.success = false;
            return result;
        } catch (IOException e) {
            result.message = "Data generation error.";
            result.success = false;
            return result;
        }
    }

    private Person generatePerson(String associatedUser, int generationYear, boolean isMale, PersonDao personDao, EventDao eventDao) throws DataAccessException, IOException {
        // Generate data for the new person
        String personID = personDao.generateID();
        String firstName = isMale ? getMaleName() : getFemaleName();
        String lastName = getSurname();
        String gender = isMale ? "m" : "f";

        // Generate events for the new person
        String birthID = eventDao.generateID();
        String deathID = eventDao.generateID();
        Location birthPlace = getLocation();
        Location deathPlace = getLocation();
        int birthYear = (int) (generationYear + Math.round(5 * Math.random()));
        int deathYear = (int) (birthYear + 60 + Math.round(30 * Math.random()));
        eventDao.insert(new Event(birthID, associatedUser, personID, birthPlace.latitude, birthPlace.longitude, birthPlace.country, birthPlace.city, "birth", birthYear));
        eventDao.insert(new Event(deathID, associatedUser, personID, deathPlace.latitude, deathPlace.longitude, deathPlace.country, deathPlace.city, "death", deathYear));

        // Return the new person
        return new Person(personID, associatedUser, firstName, lastName, gender, "", "", "");
    }

    private String getMaleName() throws IOException {
        String json = new String(Files.readAllBytes(Paths.get("json/mnames.json")));
        StringList names = (new Gson()).fromJson(json, StringList.class);
        return names.data[new Random().nextInt(names.data.length)];
    }

    private String getFemaleName() throws IOException {
        String json = new String(Files.readAllBytes(Paths.get("json/fnames.json")));
        StringList names = (new Gson()).fromJson(json, StringList.class);
        return names.data[new Random().nextInt(names.data.length)];
    }

    private String getSurname() throws IOException {
        String json = new String(Files.readAllBytes(Paths.get("json/snames.json")));
        StringList names = (new Gson()).fromJson(json, StringList.class);
        return names.data[new Random().nextInt(names.data.length)];
    }

    private Location getLocation() throws IOException {
        String json = new String(Files.readAllBytes(Paths.get("json/locations.json")));
        LocationList locations = (new Gson()).fromJson(json, LocationList.class);
        return locations.data[new Random().nextInt(locations.data.length)];
    }

    private static class StringList {
        public String[] data;
    }

    private static class LocationList {
        public Location[] data;
    }

    private static class Location {
        public String country;
        public String city;
        public float latitude;
        public float longitude;
    }
}
