package casp.web.backend.configuration;


import jakarta.validation.constraints.NotBlank;

record GlobalExceptionResponse(@NotBlank String message) {
}
