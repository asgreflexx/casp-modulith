package casp.web.backend.presentation.layer.member;

import casp.web.backend.common.enums.EntityStatus;

enum EntityStatusParam {
    ACTIVE(EntityStatus.ACTIVE),
    INACTIVE(EntityStatus.INACTIVE);
    private final EntityStatus entityStatus;

    EntityStatusParam(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    EntityStatus getEntityStatus() {
        return entityStatus;
    }
}
