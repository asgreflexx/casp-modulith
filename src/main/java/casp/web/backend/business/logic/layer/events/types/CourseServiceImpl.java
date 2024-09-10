package casp.web.backend.business.logic.layer.events.types;

import casp.web.backend.business.logic.layer.events.calendar.CalendarService;
import casp.web.backend.business.logic.layer.events.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.events.participants.SpaceService;
import casp.web.backend.data.access.layer.documents.event.participant.Space;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import casp.web.backend.presentation.layer.dtos.events.CourseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.events.CourseMapper.COURSE_MAPPER;

@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, CourseDto, Space> implements CourseService {

    private final CoTrainerService coTrainerService;

    @Autowired
    public CourseServiceImpl(final CalendarService calendarService,
                             final SpaceService participantService,
                             final BaseEventRepository eventRepository,
                             final CoTrainerService coTrainerService) {
        super(calendarService, participantService, eventRepository, Course.EVENT_TYPE, COURSE_MAPPER);
        this.coTrainerService = coTrainerService;
    }

    private void deleteCourse(final BaseEvent course) {
        coTrainerService.deleteParticipantsByBaseEventId(course.getId());
        deleteBaseEvent(course);
    }

    @Transactional
    @Override
    public CourseDto saveBaseEventDto(final CourseDto actualBaseEventDto) {
        var courseDto = super.saveBaseEventDto(actualBaseEventDto);
        courseDto.setCoTrainers(coTrainerService.saveParticipants(actualBaseEventDto.getCoTrainers(), baseEvent));
        return courseDto;
    }

    @Transactional(readOnly = true)
    @Override
    public CourseDto getBaseEventDtoById(final UUID id) {
        var courseDto = super.getBaseEventDtoById(id);
        courseDto.setCoTrainers(coTrainerService.getParticipantsByBaseEventId(baseEvent.getId()));
        return courseDto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBaseEventById(final UUID id) {
        findBaseEventNotDeleted(id).ifPresent(this::deleteCourse);
    }

    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndNotDeleted(memberId).forEach(this::deleteCourse);
    }

    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsActive(memberId).forEach(course -> {
            coTrainerService.deactivateParticipantsByBaseEventId(course.getId());
            deactivateBaseEvent(course);
        });
    }

    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsInactive(memberId).forEach(course -> {
            coTrainerService.activateParticipantsByBaseEventId(course.getId());
            activateBaseEvent(course);
        });
    }

    @Override
    public CourseDto createNewBaseEventWithOneCalendarEntry() {
        return super.createNewEventWithOneCalendarEntry(new CourseDto());
    }
}
