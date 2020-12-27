package result;

public class RegisterResult {
    /**
     * The authorization token for the created session
     */
    public String authToken;
    /**
     * The username of the newly created user
     */
    public String userName;
    /**
     * The unique string identifying the person associated
     * with the newly created user
     */
    public String personID;

    /**
     * A description of the error, if any
     */
    public String message;

    /**
     * True if the user was registered, false otherwise
      */
    public Boolean success;
}
