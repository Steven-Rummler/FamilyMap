package request;

public class RegisterRequest {
    /**
     * The username of the new user to register
     */
    public String userName;
    /**
     * The password the new user will use to log in
     */
    public String password;
    /**
     * The new user's email address
     */
    public String email;
    /**
     * The new user's first name
     */
    public String firstName;
    /**
     * The new user's last name
     */
    public String lastName;
    /**
     * The new user's gender ('m' or 'f')
     */
    public String gender;
}
