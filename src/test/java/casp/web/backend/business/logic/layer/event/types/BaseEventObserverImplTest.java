package casp.web.backend.business.logic.layer.event.types;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BaseEventObserverImplTest {
    private static final UUID MEMBER_ID = UUID.randomUUID();

    @Mock
    private CourseService courseService;
    @Mock
    private EventService eventService;
    @Mock
    private ExamService examService;

    @InjectMocks
    private BaseEventObserverImpl baseEventObserver;

    @Test
    void deleteBaseEventsByMemberId() {
        baseEventObserver.deleteBaseEventsByMemberId(MEMBER_ID);

        verify(courseService).deleteBaseEventsByMemberId(MEMBER_ID);
        verify(eventService).deleteBaseEventsByMemberId(MEMBER_ID);
        verify(examService).deleteBaseEventsByMemberId(MEMBER_ID);
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        baseEventObserver.deactivateBaseEventsByMemberId(MEMBER_ID);

        verify(courseService).deactivateBaseEventsByMemberId(MEMBER_ID);
        verify(eventService).deactivateBaseEventsByMemberId(MEMBER_ID);
        verify(examService).deactivateBaseEventsByMemberId(MEMBER_ID);
    }

    @Test
    void activateBaseEventsByMemberId() {
        baseEventObserver.activateBaseEventsByMemberId(MEMBER_ID);

        verify(courseService).activateBaseEventsByMemberId(MEMBER_ID);
        verify(eventService).activateBaseEventsByMemberId(MEMBER_ID);
        verify(examService).activateBaseEventsByMemberId(MEMBER_ID);
    }
}
