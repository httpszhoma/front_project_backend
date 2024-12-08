package zhoma.exceptions;

public class EmailNotFoundException extends RuntimeException{
    public  EmailNotFoundException(String message){
        super("This email = "+ message+ "  doesn't exist !" );
    }

}
