package casp.web.backend.business.logic.layer.event.types;


import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.data.access.layer.event.types.Exam;
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
class ExamServiceImplTest {

    @Mock
    private CalendarService calendarService;
    @Mock
    private ExamParticipantService participantService;
    @Mock
    private BaseEventRepository eventRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ExamServiceImpl examService;

    private Exam exam;

    @BeforeEach
    void setUp() {
        exam = spy(TestFixture.createExam());
    }

    @Test
    void saveBaseEvent() {
        var member = exam.getMember();
        exam.setMember(null);
        when(eventRepository.save(exam)).thenAnswer(invocation -> invocation.getArgument(0));
        when(memberRepository.findByIdAndEntityStatus(exam.getMemberId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

        assertSame(exam, examService.save(exam));
        verify(exam).setMember(member);
    }

    @Nested
    class DeleteById {
        @Test
        void exist() {
            when(eventRepository.findByIdAndEntityStatus(exam.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(exam));

            examService.deleteById(exam.getId());

            verify(participantService).deleteParticipantsByBaseEventId(exam.getId());
            verify(calendarService).deleteCalendarEntriesByBaseEventId(exam.getId());
            assertSame(EntityStatus.DELETED, exam.getEntityStatus());
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> examService.deleteById(id));
        }
    }

    @Test
    void getBaseEventsAsPage() {
        var page = new PageImpl<BaseEvent>(List.of(exam));
        var year = LocalDate.now().getYear();
        when(eventRepository.findAllByYearAndEventType(year, exam.getEventType(), Pageable.unpaged())).thenReturn(page);

        assertThat(examService.getAllByYear(year, Pageable.unpaged()).getContent()).
                singleElement()
                .isEqualTo(exam);
    }

    @Test
    void deleteBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusNotAndEventType(memberId, EntityStatus.DELETED, exam.getEventType())).thenReturn(Set.of(exam));

        examService.deleteBaseEventsByMemberId(memberId);

        verify(participantService).deleteParticipantsByBaseEventId(exam.getId());
        verify(calendarService).deleteCalendarEntriesByBaseEventId(exam.getId());
        assertSame(EntityStatus.DELETED, exam.getEntityStatus());
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.ACTIVE, exam.getEventType())).thenReturn(Set.of(exam));

        examService.deactivateBaseEventsByMemberId(memberId);

        verify(participantService).deactivateParticipantsByBaseEventId(exam.getId());
        verify(calendarService).deactivateCalendarEntriesByBaseEventId(exam.getId());
        assertSame(EntityStatus.INACTIVE, exam.getEntityStatus());
    }

    @Test
    void activateBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.INACTIVE, exam.getEventType())).thenReturn(Set.of(exam));

        examService.activateBaseEventsByMemberId(memberId);

        verify(participantService).activateParticipantsByBaseEventId(exam.getId());
        verify(calendarService).activateCalendarEntriesByBaseEventId(exam.getId());
        assertSame(EntityStatus.ACTIVE, exam.getEntityStatus());
    }

    @Nested
    class GetBaseEventById {
        @Test
        void eventExist() {
            exam.setMember(null);
            when(eventRepository.findByIdAndEntityStatus(exam.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(exam));

            assertSame(exam, examService.getOneById(exam.getId()));
            verify(memberRepository).findByIdAndEntityStatus(exam.getMemberId(), EntityStatus.ACTIVE);

        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> examService.getOneById(id));
        }
    }
}
