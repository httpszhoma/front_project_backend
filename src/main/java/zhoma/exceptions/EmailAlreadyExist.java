package zhoma.exceptions;

public class EmailAlreadyExist extends RuntimeException {

    public EmailAlreadyExist(String message) {
        super("This email already exists: " + message + ". Try another email!");
    }
}
