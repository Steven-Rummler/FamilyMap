package model;

import java.util.ArrayList;
import java.util.Objects;

public class User {
    /**
     * The unique string identifying the user
     */
    String userName;
    /**
     * The password the user uses to log in
     */
    String password;
    /**
     * The email address of the user
     */
    String email;
    /**
     * The user's first name
     */
    String firstName;
    /**
     * The user's last name
     */
    String lastName;
    /**
     * The user's gender ('m' or 'f')
     */
    String gender;
    /**
     * The person associated with this user
     */
    String personID;
    /**
     * A list of all of the events associated with the person associate with this user
     */
    //ArrayList<Event> events;

    /***
     * Accepts all of the data required for a Person object
     * and constructs the object.
     * @param userName The unique string identifying the user
     * @param password The password the user uses to log in
     * @param email The email address of the user
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param gender The user's gender ('m' or 'f')
     * @param personID The ID of the person associated with this user
     * // A list of all of the events associated with the person associate with this user
     */
    public User(String userName, String password, String email, String firstName, String lastName, String gender, String personID/*, ArrayList<Event> events*/) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
        //this.events = events;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /*public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName) &&
                Objects.equals(password, user.password) &&
                Objects.equals(email, user.email) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(personID, user.personID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password, email, firstName, lastName, gender, personID);
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", personID='" + personID +
                '}';
    }
}
