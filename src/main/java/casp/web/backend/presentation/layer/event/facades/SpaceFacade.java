package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.presentation.layer.dtos.event.participants.SpaceReadDto;
import casp.web.backend.presentation.layer.dtos.event.participants.SpaceWriteDto;

import java.util.Set;
import java.util.UUID;

public interface SpaceFacade {
    Set<SpaceReadDto> getSpacesByMemberId(UUID memberId);

    Set<SpaceReadDto> getSpacesByDogId(UUID dogId);

    SpaceReadDto save(SpaceWriteDto spaceDto);
}
