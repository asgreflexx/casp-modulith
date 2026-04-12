package casp.web.backend.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

//https://www.baeldung.com/global-error-handler-in-a-spring-rest-api
@Slf4j
@ControllerAdvice
class GlobalExceptionHandler {
    private final String internalExceptionResponse;

    @Autowired
    GlobalExceptionHandler(@Value("${casp.internal-exception-response}") String internalExceptionResponse) {
        this.internalExceptionResponse = internalExceptionResponse;
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class})
    @ResponseBody
    ProblemDetail handleBadRequestException(Exception ex) {
        log.warn("User did something wrong", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    ProblemDetail handleNoSuchElementException(NoSuchElementException ex) {
        log.warn("The user requested an element that does not exist", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
    }

    @ExceptionHandler({DuplicateKeyException.class,
            OptimisticLockingFailureException.class,
            DogHasHandlerConflictException.class,
            MemberEMailConflictException.class,
            MemberStateConflictException.class})
    @ResponseBody
    ProblemDetail handleConflictException(DataAccessException ex) {
        log.warn("A conflict encountered", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getLocalizedMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    ProblemDetail handleExceptionInternal(HttpRequestMethodNotSupportedException ex) {
        log.warn("The user made an unsupported call", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage());
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    @ResponseBody
    ProblemDetail handleExceptionInternal(Exception ex) {
        log.error("Something went wrong", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, internalExceptionResponse);
    }
}
