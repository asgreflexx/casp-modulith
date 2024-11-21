package casp.web.backend.calendar;

import casp.web.backend.calendar.presentation.BaseEventWriteRequiredFields;

@MemberReferenceDtoConstraint
@CalendarDtoConstraint
public interface BaseEventDtoWriteRequiredFields extends BaseEventWriteRequiredFields, BaseEventDtoRequiredFields {
}
