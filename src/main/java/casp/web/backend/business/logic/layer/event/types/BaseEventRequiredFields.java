package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.presentation.layer.event.BaseEventReadRequiredFields;
import jakarta.validation.constraints.NotNull;

public interface BaseEventRequiredFields extends BaseEventReadRequiredFields {
    @NotNull
    BaseEventType getEventType();

    void setEventType(@NotNull BaseEventType eventType);
}
