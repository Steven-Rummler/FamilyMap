package request;

import model.Event;
import model.Person;
import model.User;

import java.util.ArrayList;

public class LoadRequest {
    /**
     * A list of user objects to add to the database
     */
    public ArrayList<User> users;
    /**
     * A list of person objects to add to the database
     */
    public ArrayList<Person> persons;
    /**
     * A list of event objects to add to the database
     */
    public ArrayList<Event> events;
}
