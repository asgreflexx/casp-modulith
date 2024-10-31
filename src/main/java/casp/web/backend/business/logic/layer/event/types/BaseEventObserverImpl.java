package casp.web.backend.business.logic.layer.event.types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class BaseEventObserverImpl implements BaseEventObserver {
    private final Set<BaseEventService<?>> observers = new HashSet<>();

    @Autowired
    BaseEventObserverImpl(final CourseService courseService, final EventService eventService, final ExamService examService) {
        observers.addAll(Set.of(courseService, eventService, examService));
    }

    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        observers.forEach(observer -> observer.deleteBaseEventsByMemberId(memberId));
    }

    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        observers.forEach(observer -> observer.deactivateBaseEventsByMemberId(memberId));
    }

    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        observers.forEach(observer -> observer.activateBaseEventsByMemberId(memberId));
    }

    @Override
    public Set<CalendarEntryDto> getCalendarEntriesBetweenFromAndTo(final LocalDateTime from, final LocalDateTime to) {
        return observers
                .stream()
                .flatMap(observer -> observer.getCalendarEntriesBetweenFromAndTo(from, to).stream())
                .collect(Collectors.toSet());
    }
}
