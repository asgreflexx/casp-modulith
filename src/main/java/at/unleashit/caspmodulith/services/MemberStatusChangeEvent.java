package at.unleashit.caspmodulith.services;

import at.unleashit.caspmodulith.enums.EntityStatus;

import java.util.UUID;

public record MemberStatusChangeEvent(UUID id, EntityStatus entityStatus) {
}
