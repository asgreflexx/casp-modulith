package casp.web.backend.common.exception;

import jakarta.validation.constraints.NotBlank;

record GlobalExceptionResponse(@NotBlank String message) {
}
