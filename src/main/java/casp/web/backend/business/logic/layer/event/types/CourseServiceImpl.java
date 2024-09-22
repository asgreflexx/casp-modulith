package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.data.access.layer.event.types.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class CourseServiceImpl extends BaseEventServiceImpl<Course, Space> implements CourseService {

    private final CoTrainerService coTrainerService;

    @Autowired
    CourseServiceImpl(final CalendarService calendarService,
                             final SpaceService participantService,
                             final BaseEventRepository eventRepository,
                             final CoTrainerService coTrainerService) {
        super(calendarService, participantService, eventRepository, Course.EVENT_TYPE);
        this.coTrainerService = coTrainerService;
    }

    private void deleteCourse(final BaseEvent course) {
        coTrainerService.deleteParticipantsByBaseEventId(course.getId());
        deleteBaseEvent(course);
    }

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
}
