package service;

public class GoogleAPICredentials {

    static final String API_KEY = "AIzaSyAwIeFinbkgYuV5oWM0WfD3sIdwuK4c5ME";

    static void errorIfNotSpecified() {
        if (API_KEY == null) {
            System.err.println("The API Key is set to null, modify it.");
            System.exit(1);
        }
    }
}
