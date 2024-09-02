package casp.web.backend.business.logic.layer.events.types;

import casp.web.backend.business.logic.layer.events.calendar.CalendarService;
import casp.web.backend.business.logic.layer.events.dtos.CourseDto;
import casp.web.backend.business.logic.layer.events.mappers.CourseMapperImpl;
import casp.web.backend.business.logic.layer.events.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.events.participants.SpaceService;
import casp.web.backend.data.access.layer.documents.event.participant.Space;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, CourseDto, Space> implements CourseService {

    private final CoTrainerService coTrainerService;

    @Autowired
    public CourseServiceImpl(final CalendarService calendarService,
                             final SpaceService participantService,
                             final BaseEventRepository eventRepository,
                             final CoTrainerService coTrainerService) {
        super(calendarService, participantService, eventRepository, Course.EVENT_TYPE, new CourseMapperImpl());
        this.coTrainerService = coTrainerService;
    }

    private static void deleteCourse(final BaseEvent course) {
        // TODO delete cotrainers (event);
        deleteBaseEvent(course);
    }

    @Transactional
    @Override
    public CourseDto saveBaseEventDto(final CourseDto actualBaseEventDto) {
        var courseDto = super.saveBaseEventDto(actualBaseEventDto);
        courseDto.setCoTrainers(coTrainerService.saveParticipants(actualBaseEventDto.getCoTrainers()));
        return courseDto;
    }

    @Transactional(readOnly = true)
    @Override
    public CourseDto getBaseEventDtoById(final UUID id) {
        var courseDto = super.getBaseEventDtoById(id);
        courseDto.setCoTrainers(coTrainerService.getParticipantsByEvent(mapper.dtoToDocument(courseDto)));
        return courseDto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBaseEventById(final UUID id) {
        findBaseEventNotDeleted(id).ifPresent(CourseServiceImpl::deleteCourse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndNotDeleted(memberId).forEach(CourseServiceImpl::deleteCourse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsActive(memberId).forEach(course -> {
            // TODO deactivate coTrainers
            deactivateBaseEvent(course);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        findAllByMemberIdAndIsInactive(memberId).forEach(course -> {
            // TODO activate coTrainers
            activateBaseEvent(course);
        });
    }

    @Override
    public CourseDto createNewBaseEventWithOneCalendarEntry() {
        return super.createNewEventWithOneCalendarEntry(new CourseDto());
    }
}
