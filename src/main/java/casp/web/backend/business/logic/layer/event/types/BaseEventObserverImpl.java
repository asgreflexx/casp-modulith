package casp.web.backend.business.logic.layer.event.types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Service
class BaseEventObserverImpl implements BaseEventObserver {
    private final Set<BaseEventService<?>> observers = new HashSet<>();

    @Autowired
    BaseEventObserverImpl(CourseService courseService, EventService eventService, ExamService examService) {
        observers.addAll(Set.of(courseService, eventService, examService));
    }

    @Override
    public void deleteBaseEventsByMemberId(UUID memberId) {
        observers.forEach(observer -> observer.deleteBaseEventsByMemberId(memberId));
    }

    @Override
    public void deactivateBaseEventsByMemberId(UUID memberId) {
        observers.forEach(observer -> observer.deactivateBaseEventsByMemberId(memberId));
    }

    @Override
    public void activateBaseEventsByMemberId(UUID memberId) {
        observers.forEach(observer -> observer.activateBaseEventsByMemberId(memberId));
    }

    @Override
    public Stream<CalendarEntryDto> getCalendarEntriesBetweenFromAndTo(LocalDateTime from, LocalDateTime to) {
        return observers
                .parallelStream()
                .flatMap(observer -> observer.getCalendarEntriesBetweenFromAndTo(from, to));
    }
}
