package casp.web.backend.common.exception;

public class MemberEMailConflictException extends RuntimeException {
    public MemberEMailConflictException(String message) {
        super(message);
    }
}
