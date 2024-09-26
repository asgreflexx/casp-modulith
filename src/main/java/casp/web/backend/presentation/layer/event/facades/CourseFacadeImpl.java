package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static casp.web.backend.presentation.layer.dtos.event.participants.CoTrainerMapper.CO_TRAINER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.SpaceMapper.SPACE_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.CourseMapper.COURSE_MAPPER;

@Service
class CourseFacadeImpl implements CourseFacade {
    private final CoTrainerService coTrainerService;
    private final SpaceService spaceService;

    @Autowired
    CourseFacadeImpl(final CoTrainerService coTrainerService, final SpaceService spaceService) {
        this.coTrainerService = coTrainerService;
        this.spaceService = spaceService;
    }

    @Override
    public CourseDto mapBaseEventToDto(final BaseEvent baseEvent) {
        var courseDto = COURSE_MAPPER.toDto((Course) baseEvent);
        setCoTrainers(courseDto);
        setSpaces(courseDto);
        return courseDto;
    }

    private void setCoTrainers(final CourseDto courseDto) {
        var coTrainerDtoSet = coTrainerService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(courseDto.getId())
                .stream()
                .map(CO_TRAINER_MAPPER::toDto)
                .collect(Collectors.toSet());
        courseDto.setCoTrainers(coTrainerDtoSet);
    }

    private void setSpaces(final CourseDto courseDto) {
        var spaceDtoSet = spaceService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(courseDto.getId())
                .stream()
                .map(SPACE_MAPPER::toDto)
                .collect(Collectors.toSet());
        courseDto.setParticipants(spaceDtoSet);
    }
}
