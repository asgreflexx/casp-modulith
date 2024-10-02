package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private CalendarService calendarService;
    @Mock
    private EventParticipantService participantService;
    @Mock
    private BaseEventRepository eventRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;

    @BeforeEach
    void setUp() {
        event = spy(TestFixture.createEvent());
    }

    @Test
    void saveBaseEvent() {
        var member = event.getMember();
        event.setMember(null);
        when(eventRepository.save(event)).thenAnswer(invocation -> invocation.getArgument(0));
        when(memberRepository.findByIdAndEntityStatus(event.getMemberId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

        assertSame(event, eventService.save(event));
        verify(event).setMember(member);
    }

    @Nested
    class DeleteById {
        @Test
        void eventExist() {
            when(eventRepository.findByIdAndEntityStatus(event.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(event));

            eventService.deleteById(event.getId());

            verify(participantService).deleteParticipantsByBaseEventId(event.getId());
            verify(calendarService).deleteCalendarEntriesByBaseEventId(event.getId());
            assertSame(EntityStatus.DELETED, event.getEntityStatus());
        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> eventService.deleteById(id));
        }
    }

    @Test
    void getBaseEventsAsPage() {
        var page = new PageImpl<BaseEvent>(List.of(event));
        var year = LocalDate.now().getYear();
        when(eventRepository.findAllByYearAndEventType(year, event.getEventType(), Pageable.unpaged())).thenReturn(page);

        assertThat(eventService.getAllByYear(year, Pageable.unpaged()).getContent()).
                singleElement()
                .isEqualTo(event);
    }

    @Test
    void deleteBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusNotAndEventType(memberId, EntityStatus.DELETED, event.getEventType())).thenReturn(Set.of(event));

        eventService.deleteBaseEventsByMemberId(memberId);

        verify(participantService).deleteParticipantsByBaseEventId(event.getId());
        verify(calendarService).deleteCalendarEntriesByBaseEventId(event.getId());
        assertSame(EntityStatus.DELETED, event.getEntityStatus());
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.ACTIVE, event.getEventType())).thenReturn(Set.of(event));

        eventService.deactivateBaseEventsByMemberId(memberId);

        verify(participantService).deactivateParticipantsByBaseEventId(event.getId());
        verify(calendarService).deactivateCalendarEntriesByBaseEventId(event.getId());
        assertSame(EntityStatus.INACTIVE, event.getEntityStatus());
    }

    @Test
    void activateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.INACTIVE, event.getEventType())).thenReturn(Set.of(event));

        eventService.activateBaseEventsByMemberId(memberId);

        verify(participantService).activateParticipantsByBaseEventId(event.getId());
        verify(calendarService).activateCalendarEntriesByBaseEventId(event.getId());
        assertSame(EntityStatus.ACTIVE, event.getEntityStatus());
    }

    @Nested
    class GetBaseEventById {
        @Test
        void eventExist() {
            event.setMember(null);
            when(eventRepository.findByIdAndEntityStatus(event.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(event));

            assertSame(event, eventService.getOneById(event.getId()));
            verify(memberRepository).findByIdAndEntityStatus(event.getMemberId(), EntityStatus.ACTIVE);
        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> eventService.getOneById(id));
        }
    }
}
