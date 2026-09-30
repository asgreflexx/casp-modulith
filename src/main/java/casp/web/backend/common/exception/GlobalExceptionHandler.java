package casp.web.backend.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

//https://www.baeldung.com/global-error-handler-in-a-spring-rest-api
@Slf4j
@ControllerAdvice
class GlobalExceptionHandler {
    private static final String VALIDATION_ERROR_DETAILS = "Field: %s, rejected value: %s, message: %s";
    private final String internalExceptionResponse;

    @Autowired
    GlobalExceptionHandler(@Value("${casp.internal-exception-response}") String internalExceptionResponse) {
        this.internalExceptionResponse = internalExceptionResponse;
    }

    // MethodArgumentTypeMismatchException tested: DogHasHandlerRestControllerExceptionIntTest.GetDogHasHandlerById.badRequest
    // MethodArgumentNotValidException tested: DogHasHandlerRestControllerExceptionIntTest.SaveDogHasHandler.BadRequest.bodyInvalid
    // ConstraintViolationException tested: DogHasHandlerRestControllerExceptionIntTest.getDogHasHandlersByHandlerIds
    // MissingRequestHeaderException tested: CourseRestControllerExceptionIntTest.UpdateSpaces.BadRequest.missingHeader
    // HttpMessageNotReadableException tested: CourseRestControllerExceptionIntTest.UpdateSpaces.BadRequest.badBody
    @ExceptionHandler({MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestHeaderException.class,
            HttpMessageNotReadableException.class})
    @ResponseBody
    ProblemDetail handleBadRequestException(Exception ex) {
        log.warn("User did something wrong", ex);
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        if (ex instanceof MethodArgumentNotValidException exception) {
            var message = exception
                    .getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(fieldError -> String.format(VALIDATION_ERROR_DETAILS,
                            fieldError.getField(),
                            fieldError.getRejectedValue(),
                            fieldError.getDefaultMessage()))
                    .collect(Collectors.joining("; "));
            problemDetail.setDetail(message);
        } else {
            problemDetail.setDetail(ex.getLocalizedMessage());
        }
        return problemDetail;
    }

    // Tested: DogHasHandlerRestControllerExceptionIntTest.GetDogHasHandlerById.notFound
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
    ProblemDetail handleConflictException(Exception ex) {
        log.warn("A conflict encountered", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getLocalizedMessage());
    }

    // Tested: GlobalExceptionHandlerIntTest.methodNotSupportedException
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    ProblemDetail methodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("The user made an unsupported call", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage());
    }

    // Tested: GlobalExceptionHandlerIntTest.handleException
    @ExceptionHandler(Exception.class)
    @ResponseBody
    ProblemDetail handleException(Exception ex) {
        log.error("Something went wrong", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, internalExceptionResponse);
    }
}
