package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.presentation.layer.event.BaseEventWriteRequiredFields;

@MemberReferenceDtoConstraint
@CalendarDtoConstraint
public interface BaseEventDtoWriteRequiredFields extends BaseEventWriteRequiredFields, BaseEventDtoRequiredFields {
}
