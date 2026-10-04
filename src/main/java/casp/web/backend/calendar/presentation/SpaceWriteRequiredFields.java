package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.data.participants.EventResponse;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.validation.Payment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface SpaceWriteRequiredFields extends Payment {
    @Valid
    @NotNull
    DogHasHandlerReference getDogHasHandler();

    void setDogHasHandler(@Valid @NotNull DogHasHandlerReference dogHasHandler);

    String getNote();

    void setNote(String note);

    @NotNull
    EventResponse getResponse();

    void setResponse(@NotNull EventResponse response);
}
