package zhoma.exceptions;

public class UserNotVerifiedException extends RuntimeException{

    public UserNotVerifiedException(){
        super("Account not verified. Please verify your account.");

    }
}
