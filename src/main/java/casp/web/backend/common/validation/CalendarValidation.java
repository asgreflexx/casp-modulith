package casp.web.backend.common.validation;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@CalendarFromToConstraint
public interface CalendarValidation {
    @NotNull
    LocalDateTime getEntryFrom();

    void setEntryFrom(@NotNull LocalDateTime entryFrom);

    @NotNull
    LocalDateTime getEntryTo();

    void setEntryTo(@NotNull LocalDateTime entryTo);
}
