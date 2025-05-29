package casp.web.backend.member;

import casp.web.backend.common.enums.EntityStatus;

public interface MemberDtoRequiredFields extends MemberRequiredFields {
    EntityStatus getEntityStatus();

    void setEntityStatus(EntityStatus entityStatus);
}
