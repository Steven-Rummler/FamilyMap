package result;

import java.util.ArrayList;

public class PersonsResult {
    /**
     * A list containing data for all of the person objects
     * associated with the current user
     */
    public ArrayList<Object> data;

    /**
     * A description of the error, if any
     */
    public String message;

    /**
     * True if the person objects associated with the current user
     * were successfully retrieved, false otherwise
     */
    public Boolean success;
}
