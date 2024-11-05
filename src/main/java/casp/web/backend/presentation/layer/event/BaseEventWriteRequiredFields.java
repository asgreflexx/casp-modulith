package casp.web.backend.presentation.layer.event;

import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.data.access.layer.event.options.RecurrenceOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public interface BaseEventWriteRequiredFields {
    @NotBlank
    String getName();

    void setName(@NotBlank String name);

    String getDescription();

    void setDescription(String description);

    String getLocation();

    void setLocation(String location);

    @Valid
    MemberReference getMember();

    void setMember(@Valid MemberReference member);

    @Valid
    RecurrenceOption getRecurrenceOption();

    void setRecurrenceOption(@Valid RecurrenceOption recurrenceOption);
}
