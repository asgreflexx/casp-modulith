package casp.web.backend.presentation.layer.handler;


import jakarta.validation.constraints.NotBlank;

class GlobalExceptionResponse {

    @NotBlank
    private final String message;

    /**
     * This is used for internal exceptions
     */
    GlobalExceptionResponse() {
        this("An internal error has occurred");
    }

    GlobalExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
