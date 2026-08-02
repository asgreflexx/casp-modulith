package casp.web.backend.common.exception;

public class MemberStateConflictException extends RuntimeException {
    public MemberStateConflictException(String message) {
        super(message);
    }
}
