package casp.web.backend.presentation.layer.configuration;


import jakarta.validation.constraints.NotBlank;

record GlobalExceptionResponse(@NotBlank String message) {
}
