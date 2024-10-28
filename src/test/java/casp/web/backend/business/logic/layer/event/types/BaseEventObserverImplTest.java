package casp.web.backend.business.logic.layer.event.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BaseEventObserverImplTest {
    @Spy
    private CourseService courseService;
    @Spy
    private EventService eventService;
    @Spy
    private ExamService examService;

    private UUID memberId;
    @InjectMocks
    private BaseEventObserverImpl observer;


    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
    }

    @Test
    void deleteBaseEventsByMemberId() {
        observer.deleteBaseEventsByMemberId(memberId);

        verify(courseService).deleteBaseEventsByMemberId(memberId);
        verify(eventService).deleteBaseEventsByMemberId(memberId);
        verify(examService).deleteBaseEventsByMemberId(memberId);
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        observer.deactivateBaseEventsByMemberId(memberId);

        verify(courseService).deactivateBaseEventsByMemberId(memberId);
        verify(eventService).deactivateBaseEventsByMemberId(memberId);
        verify(examService).deactivateBaseEventsByMemberId(memberId);
    }

    @Test
    void activateBaseEventsByMemberId() {
        observer.activateBaseEventsByMemberId(memberId);

        verify(courseService).activateBaseEventsByMemberId(memberId);
        verify(eventService).activateBaseEventsByMemberId(memberId);
        verify(examService).activateBaseEventsByMemberId(memberId);
    }
}
