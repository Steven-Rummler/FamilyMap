package result;

public class LoginResult {
    /**
     * The authorization token for the successful login
     */
    public String authToken;
    /**
     * The unique string identifying the user who logged in
     */
    public String userName;
    /**
     * The unique string identifying the person associated
     * with the user who logged in
     */
    public String personID;

    /**
     * A message describing the error, if any
     */
    public String message;

    /**
     * True if the user successfully logged in, false otherwise
     */
    public Boolean success;
}
