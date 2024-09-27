package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.business.logic.layer.event.types.CourseService;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

import static casp.web.backend.presentation.layer.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.CoTrainerMapper.CO_TRAINER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.participants.SpaceMapper.SPACE_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.CourseMapper.COURSE_MAPPER;

@Service
class CourseFacadeImpl implements CourseFacade {
    private static final Logger LOG = LoggerFactory.getLogger(CourseFacadeImpl.class);
    private final CoTrainerService coTrainerService;
    private final SpaceService spaceService;
    private final CalendarService calendarService;
    private final CourseService courseService;

    @Autowired
    CourseFacadeImpl(final CoTrainerService coTrainerService,
                     final SpaceService spaceService,
                     final CalendarService calendarService,
                     final CourseService courseService) {
        this.coTrainerService = coTrainerService;
        this.spaceService = spaceService;
        this.calendarService = calendarService;
        this.courseService = courseService;
    }

    @Override
    public CourseDto mapDocumentToDto(final BaseEvent baseEvent) {
        if (baseEvent instanceof Course course) {
            var courseDto = COURSE_MAPPER.toDto(course);
            setCoTrainers(courseDto);
            setSpaces(courseDto);
            return courseDto;
        } else {
            var msg = "The parameter %s is not a course".formatted(baseEvent.getEventType());
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public void save(final CourseDto courseDto) {
        var course = COURSE_MAPPER.toDocument(courseDto);

        calendarService.replaceCalendarEntries(course, CALENDAR_MAPPER.toDocumentList(courseDto.getCalendarEntries()));
        spaceService.replaceParticipants(course, courseDto.getParticipantsIdToWrite());
        coTrainerService.replaceParticipants(course, courseDto.getCoTrainersIdToWrite());

        courseService.save(course);
    }

    @Override
    public CourseDto getById(final UUID id) {
        var course = courseService.getById(id);
        return mapDocumentToDto(course);
    }

    private void setCoTrainers(final CourseDto courseDto) {
        var coTrainerDtoSet = coTrainerService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(courseDto.getId())
                .stream()
                .map(CO_TRAINER_MAPPER::toDto)
                .collect(Collectors.toSet());
        courseDto.setCoTrainersToRead(coTrainerDtoSet);
    }

    private void setSpaces(final CourseDto courseDto) {
        var spaceDtoSet = spaceService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(courseDto.getId())
                .stream()
                .map(SPACE_MAPPER::toDto)
                .collect(Collectors.toSet());
        courseDto.setParticipantsToRead(spaceDtoSet);
    }
}
