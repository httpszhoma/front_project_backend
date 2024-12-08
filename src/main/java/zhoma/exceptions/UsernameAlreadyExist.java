package zhoma.exceptions;

public class UsernameAlreadyExist extends RuntimeException {

    public UsernameAlreadyExist(String message) {
        super("This username already exists: " + message + ". Try another username!");
    }
}
