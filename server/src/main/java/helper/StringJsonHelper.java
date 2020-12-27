package helper;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StringJsonHelper {
    /**
     * Reads in a string from an input stream
     * @param is the stream to read from
     * @return the processed string
     * @throws IOException
     */
    public static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /**
     * Outputs a string to an output stream
     * @param str the string to output
     * @param os the stream to output to
     * @throws IOException
     */
    public static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }

    /**
     * Converts a JSON string to an Object
     * @param value the JSON String
     * @param returnType the type of the Object to return
     * @return the Object created from the JSON string
     */
    public static <T> T deserialize(String value, Class<T> returnType) {
        return (new Gson()).fromJson(value, returnType);
    }

    /**
     * Converts an Object to a JSON String
     * @param object the Object to convert
     * @return the JSON string created from the Object
     */
    public static String serialize(Object object) {
        return (new Gson()).toJson(object);
    }

    /**
     * Generates a random string of length n from alphanumeric characters
     * @param n the length of the string
     * @return a random string
     */
    public static String getRandomString(int n) {
        String availableChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int) (availableChars.length() * Math.random());
            sb.append(availableChars.charAt(index));
        }

        return sb.toString();
    }
}
