package result;

import java.util.ArrayList;

public class EventsResult {
    /**
     * A list containing all events associated with the current user
     */
    public ArrayList<Object> data;

    /**
     * A message describing the error
     */
    public String message;

    /**
     * True if the list of events was retrieved, false otherwise
     */
    public Boolean success;
}
