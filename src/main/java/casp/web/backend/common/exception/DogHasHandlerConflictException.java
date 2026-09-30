package casp.web.backend.common.exception;

public class DogHasHandlerConflictException extends RuntimeException {
    public DogHasHandlerConflictException(String message) {
        super(message);
    }
}
