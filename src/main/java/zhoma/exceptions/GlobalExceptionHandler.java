package zhoma.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zhoma.responses.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExist.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExist(EmailAlreadyExist ex){
        ErrorResponse errorResponse = new ErrorResponse("Email Error", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);


    }

    @ExceptionHandler(UsernameAlreadyExist.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExist(UsernameAlreadyExist ex){
        ErrorResponse errorResponse = new ErrorResponse("Username Error", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);


    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotFoundException(EmailNotFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse("Email Error", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(VerificationCodeExpiredException.class)

    public ResponseEntity<ErrorResponse> handleEmailNotFoundException(VerificationCodeExpiredException ex){
        ErrorResponse errorResponse = new ErrorResponse("Verification Code Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(UserNotVerifiedException.class)

    public ResponseEntity<ErrorResponse> handleEmailNotFoundException(UserNotVerifiedException ex){
        ErrorResponse errorResponse = new ErrorResponse("Verification Code Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAllException(RuntimeException ex){
        ErrorResponse errorResponse = new ErrorResponse("Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }


}
