package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
class BaseEventObserverImpl implements BaseEventObserver {
    private final Map<BaseEventType, BaseEventService<?>> observerMap = new EnumMap<>(BaseEventType.class);

    @Autowired
    BaseEventObserverImpl(CourseService courseService, EventService eventService, ExamService examService) {
        observerMap.put(BaseEventType.COURSE, courseService);
        observerMap.put(BaseEventType.EVENT, eventService);
        observerMap.put(BaseEventType.EXAM, examService);
    }

    @Override
    public void deleteBaseEventsByMemberId(UUID memberId) {
        executeOperation(service -> service.deleteBaseEventsByMemberId(memberId));
    }

    @Override
    public void deactivateBaseEventsByMemberId(UUID memberId) {
        executeOperation(service -> service.deactivateBaseEventsByMemberId(memberId));
    }

    @Override
    public void activateBaseEventsByMemberId(UUID memberId) {
        executeOperation(service -> service.activateBaseEventsByMemberId(memberId));
    }

    @Override
    public Stream<CalendarEntryDto> getCalendarEntriesBetweenFromAndTo(LocalDateTime from, LocalDateTime to, Set<BaseEventType> eventTypeSet) {
        return observerMap.entrySet()
                .parallelStream()
                .filter(observer -> eventTypeSet.isEmpty() || eventTypeSet.contains(observer.getKey()))
                .flatMap(observer -> observer.getValue().getCalendarEntriesBetweenFromAndTo(from, to));
    }

    private void executeOperation(Consumer<BaseEventService<?>> operation) {
        observerMap.values().forEach(operation);
    }
}
