package result;

public class PersonResult {
    /**
     * The unique string identifying the user
     * associated with this person
     */
    public String associatedUsername;
    /**
     * The unique string identifying this person
     */
    public String personID;
    /**
     * This person's first name
     */
    public String firstName;
    /**
     * This person's last name
     */
    public String lastName;
    /**
     * This person's gender ('m' or 'f')
     */
    public String gender;
    /** The unique string identifying the person
     * object for this person's father
     */
    public String fatherID;
    /** The unique string identifying the person
     * object for this person's mother
     */
    public String motherID;
    /** The unique string identifying the person
     * object for this person's spouse
     */
    public String spouseID;

    /**
     * A description of the error, if any
     */
    public String message;

    /**
     * True if the person was successfully retrieved
     * from the database, false otherwise
     */
    public Boolean success;
}
