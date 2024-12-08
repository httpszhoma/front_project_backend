package zhoma.exceptions;

public class VerificationCodeExpiredException extends RuntimeException{
    public VerificationCodeExpiredException(String message){
        super("Verification code of this email = "+  message+ "  already expired!");

    }

}
