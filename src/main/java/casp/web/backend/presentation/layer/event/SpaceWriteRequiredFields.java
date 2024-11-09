package casp.web.backend.presentation.layer.event;

import casp.web.backend.common.enums.EventResponse;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.validation.Payment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public interface SpaceWriteRequiredFields extends Payment {
    @Valid
    @NotNull
    DogHasHandlerReference getDogHasHandler();

    void setDogHasHandler(@Valid @NotNull DogHasHandlerReference dogHasHandler);

    String getNote();

    void setNote(String note);

    void setPaidPrice(@PositiveOrZero @Digits(integer = 9, fraction = 2) double paidPrice);

    void setPaidDate(LocalDate paidDate);

    @NotNull
    EventResponse getResponse();

    void setResponse(@NotNull EventResponse response);
}
