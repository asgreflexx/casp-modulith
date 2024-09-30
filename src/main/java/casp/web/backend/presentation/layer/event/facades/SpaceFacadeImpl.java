package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.business.logic.layer.event.types.CourseService;
import casp.web.backend.presentation.layer.dtos.event.participants.SpaceReadDto;
import casp.web.backend.presentation.layer.dtos.event.participants.SpaceWriteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.presentation.layer.dtos.event.participants.SpaceMapper.SPACE_MAPPER;

@Service
class SpaceFacadeImpl implements SpaceFacade {
    private final SpaceService spaceService;
    private final CourseService courseService;

    @Autowired
    SpaceFacadeImpl(final SpaceService spaceService, final CourseService courseService) {
        this.spaceService = spaceService;
        this.courseService = courseService;
    }

    @Override
    public Set<SpaceReadDto> getSpacesByMemberId(final UUID memberId) {
        return spaceService.getSpacesByMemberId(memberId)
                .stream()
                .map(SPACE_MAPPER::toReadDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<SpaceReadDto> getSpacesByDogId(final UUID dogId) {
        return spaceService.getSpacesByDogId(dogId)
                .stream()
                .map(SPACE_MAPPER::toReadDto)
                .collect(Collectors.toSet());
    }

    @Override
    public SpaceReadDto save(final SpaceWriteDto spaceDto) {
        var course = courseService.getOneById(spaceDto.getCourseId());
        var space = SPACE_MAPPER.fromWriteDto(spaceDto);
        space.setBaseEvent(course);
        return SPACE_MAPPER.toReadDto(spaceService.saveParticipant(space));
    }
}
