package Exceptions;

public class PageNotFoundException extends ApiError{
    public PageNotFoundException(String message) {
        super(message);
    }
}
