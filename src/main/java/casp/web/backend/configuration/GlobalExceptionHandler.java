package casp.web.backend.configuration;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;


//https://www.baeldung.com/global-error-handler-in-a-spring-rest-api
@ControllerAdvice
class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final String internalExceptionResponse;

    @Autowired
    GlobalExceptionHandler(@Value("${casp.internal-exception-response}") String internalExceptionResponse) {
        this.internalExceptionResponse = internalExceptionResponse;
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    GlobalExceptionResponse handleBadRequestException(Exception ex) {
        LOG.warn("User did something wrong", ex);
        return new GlobalExceptionResponse(ex.getLocalizedMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    GlobalExceptionResponse handleNoSuchElementException(NoSuchElementException ex) {
        LOG.warn("The user requested an element that does not exist", ex);
        return new GlobalExceptionResponse(ex.getLocalizedMessage());
    }

    @ExceptionHandler({
            DuplicateKeyException.class,
            OptimisticLockingFailureException.class
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    GlobalExceptionResponse handleConflictException(DataAccessException ex) {
        LOG.warn("A conflict encountered", ex);
        return new GlobalExceptionResponse(ex.getLocalizedMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    GlobalExceptionResponse handleExceptionInternal(HttpRequestMethodNotSupportedException ex) {
        LOG.warn("The user made an unsupported call", ex);
        return new GlobalExceptionResponse(ex.getLocalizedMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    GlobalExceptionResponse handleExceptionInternal(RuntimeException ex) {
        LOG.error("Something went wrong", ex);
        return new GlobalExceptionResponse(internalExceptionResponse);
    }
}
