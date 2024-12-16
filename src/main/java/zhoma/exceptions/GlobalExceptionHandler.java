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
    @ExceptionHandler(ProductNotFountException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFount(ProductNotFountException ex){
        ErrorResponse errorResponse = new ErrorResponse("Product error ", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(BrandNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBrandNotFount(BrandNotFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse("Brand error ", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(CategoryNotFoundException.class)

    public ResponseEntity<ErrorResponse> handleCategoryNotFount(CategoryNotFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse("Category error ", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAllException(RuntimeException ex){
        ErrorResponse errorResponse = new ErrorResponse("Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }


}
