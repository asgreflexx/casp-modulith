package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.presentation.BaseEventReadRequiredFields;
import jakarta.validation.constraints.NotNull;

public interface BaseEventRequiredFields extends BaseEventReadRequiredFields {
    @NotNull
    BaseEventType getEventType();

    void setEventType(@NotNull BaseEventType eventType);
}
