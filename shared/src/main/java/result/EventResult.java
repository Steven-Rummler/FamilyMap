package result;

public class EventResult {
    /**
     * The username of the user with whom this event is associated
     */
    public String associatedUsername;
    /**
     * The unique string identifying this event
     */
    public String eventID;
    /**
     * The unique string identifying the person with whom this event is associated
      */
    public String personID;
    /**
     * The latitude at which this event occurred
     */
    public Float latitude;
    /**
     * The longitude at which this event occurred
     */
    public Float longitude;
    /**
     * The country in which this event occurred
     */
    public String country;
    /**
     * The city in which this event occurred
     */
    public String city;
    /**
     * The type of this event (birth, death, marriage, etc.)
     */
    public String eventType;
    /**
     * The year in which this event occurred
     */
    public Integer year;

    /**
     * A message describing the failure of the operation
     */
    public String message;

    /**
     * True if the event was found in the database, false otherwise
     */
    public Boolean success;
}
