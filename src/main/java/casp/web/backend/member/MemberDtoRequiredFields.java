package casp.web.backend.member;

import casp.web.backend.calendar.SpaceDto;
import casp.web.backend.common.enums.EntityStatus;

import java.util.Set;

public interface MemberDtoRequiredFields extends MemberRequiredFields {
    EntityStatus getEntityStatus();

    void setEntityStatus(EntityStatus entityStatus);

    Set<SpaceDto> getSpaces();

    void setSpaces(Set<SpaceDto> spaces);
}
