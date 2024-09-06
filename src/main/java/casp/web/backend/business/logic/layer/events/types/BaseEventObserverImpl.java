package casp.web.backend.business.logic.layer.events.types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
class BaseEventObserverImpl implements BaseEventObserver {
    private final Set<BaseEventService<?, ?, ?>> observers = new HashSet<>();

    @Autowired
    BaseEventObserverImpl(final CourseService courseService, final EventService eventService, final ExamService examService) {
        observers.add(courseService);
        observers.add(eventService);
        observers.add(examService);
    }

    @Transactional
    @Override
    public void deleteBaseEventsByMemberId(final UUID memberId) {
        observers.forEach(observer -> observer.deleteBaseEventsByMemberId(memberId));
    }

    @Transactional
    @Override
    public void deactivateBaseEventsByMemberId(final UUID memberId) {
        observers.forEach(observer -> observer.deactivateBaseEventsByMemberId(memberId));
    }

    @Transactional
    @Override
    public void activateBaseEventsByMemberId(final UUID memberId) {
        observers.forEach(observer -> observer.activateBaseEventsByMemberId(memberId));
    }
}
