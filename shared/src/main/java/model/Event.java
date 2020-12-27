package model;

import java.util.Objects;

public class Event {
    /**
     * The unique string identifying the event
     */
    String eventID;
    /**
     * The username of the user associated with this event
     */
    String associatedUsername;
    /**
     * The person ID of the person whose event this is
     */
    String personID;
    /**
     * The latitude at which the event occurred
     */
    Float latitude;
    /**
     * The longitude at which the event occurred
     */
    Float longitude;
    /**
     * The country in which the event occurred
     */
    String country;
    /**
     * The city in which the event occurred
     */
    String city;
    /**
     * The type of event (birth, death, baptism, etc.)
     */
    String eventType;
    /**
     * The year in which the event occurred
     */
    Integer year;

    /***
     * Accepts all of the data required for an Event object
     * and constructs the object.
     * @param eventID The unique string identifying the event
     * @param associatedUsername The username of the user associated with this event
     * @param personID The ID of the person whose event this is
     * @param latitude The latitude at which the event occurred
     * @param longitude The longitude at which the event occurred
     * @param country The country in which the event occurred
     * @param city The city in which the event occurred
     * @param eventType The type of event (birth, death, baptism, etc.)
     * @param year The year in which the event occurred
     */
    public Event(String eventID, String associatedUsername, String personID, Float latitude, Float longitude, String country, String city, String eventType, Integer year) {
        this.eventID = eventID;
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventID, event.eventID) &&
                Objects.equals(associatedUsername, event.associatedUsername) &&
                Objects.equals(personID, event.personID) &&
                Objects.equals(latitude, event.latitude) &&
                Objects.equals(longitude, event.longitude) &&
                Objects.equals(country, event.country) &&
                Objects.equals(city, event.city) &&
                Objects.equals(eventType, event.eventType) &&
                Objects.equals(year, event.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventID, associatedUsername, personID, latitude, longitude, country, city, eventType, year);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventID='" + eventID + '\'' +
                ", associatedUsername=" + associatedUsername +
                ", personID=" + personID +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", eventType='" + eventType + '\'' +
                ", year=" + year +
                '}';
    }
}
