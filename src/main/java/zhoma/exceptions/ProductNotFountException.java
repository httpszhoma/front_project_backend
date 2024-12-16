package zhoma.exceptions;

public class ProductNotFountException extends RuntimeException{
    public ProductNotFountException(String message){
        super(message);
    }
}