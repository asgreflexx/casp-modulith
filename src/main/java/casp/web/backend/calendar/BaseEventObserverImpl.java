package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

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
        performOperationOnAllServices(service -> service.deleteBaseEventsByMemberId(memberId));
    }

    @Override
    public void deactivateBaseEventsByMemberId(UUID memberId) {
        performOperationOnAllServices(service -> service.deactivateBaseEventsByMemberId(memberId));
    }

    @Override
    public void activateBaseEventsByMemberId(UUID memberId) {
        performOperationOnAllServices(service -> service.activateBaseEventsByMemberId(memberId));
    }

    @Override
    public List<CalendarEntryDto> getCalendarEntriesBetweenFromAndToOrMemberId(LocalDateTime from, LocalDateTime to, UUID memberId) {
        return observerMap.entrySet()
                .parallelStream()
                .flatMap(observer -> observer.getValue().getCalendarEntriesBetweenFromAndToOrMemberId(from, to, memberId))
                .sorted()
                .toList();
    }

    private void performOperationOnAllServices(Consumer<BaseEventService<?>> operation) {
        observerMap.values().forEach(operation);
    }
}
