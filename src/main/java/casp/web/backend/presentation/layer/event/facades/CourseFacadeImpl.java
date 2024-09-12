package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.CoTrainerMapper.CO_TRAINER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.SpaceMapper.SPACE_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.CourseMapper.COURSE_MAPPER;
import static casp.web.backend.presentation.layer.dtos.member.MemberMapper.MEMBER_MAPPER;

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
        var coTrainerDtoSet = coTrainerService.getActiveCoTrainersIfMembersAreActive(courseDto.getId())
                .stream()
                .map(pm -> {
                    var coTrainerDto = CO_TRAINER_MAPPER.toDto((CoTrainer) pm.participant());
                    coTrainerDto.setMember(MEMBER_MAPPER.toDto(pm.member()));
                    coTrainerDto.setBaseEvent(null);
                    return coTrainerDto;
                }).collect(Collectors.toSet());

        courseDto.setCoTrainers(coTrainerDtoSet);
    }

    private void setSpaces(final CourseDto courseDto) {
        var spaceDtoSet = spaceService.getActiveSpacesIfDogHasHandlersAreActive(courseDto.getId())
                .stream()
                .map(pd -> {
                    var spaceDto = SPACE_MAPPER.toDto((Space) pd.participant());
                    spaceDto.setDogHasHandler(DOG_HAS_HANDLER_MAPPER.toDto(pd.dogHasHandler()));
                    spaceDto.setBaseEvent(null);
                    return spaceDto;
                })
                .collect(Collectors.toSet());
        courseDto.setParticipants(spaceDtoSet);
    }
}
