package casp.web.backend.presentation.layer.event;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.documents.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.participants.Space;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.data.access.layer.documents.member.Member;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import casp.web.backend.data.access.layer.repositories.CalendarRepository;
import casp.web.backend.data.access.layer.repositories.MemberRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;
import casp.web.backend.presentation.layer.dtos.event.types.EventDto;
import casp.web.backend.presentation.layer.dtos.event.types.ExamDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CalendarRestControllerTest {
    private static final String CALENDAR_URL_PREFIX = "/calendar";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BaseEventRepository baseEventRepository;
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private MemberRepository memberRepository;

    private List<Calendar> calendarList;
    private CoTrainer coTrainer;
    private Space space;
    private EventParticipant eventParticipant;
    private ExamParticipant examParticipant;
    private Member member;

    @BeforeEach
    void setUp() {
        calendarList = new ArrayList<>();
        calendarRepository.deleteAll();
        baseParticipantRepository.deleteAll();
        baseEventRepository.deleteAll();
        memberRepository.deleteAll();

        member = TestFixture.createValidMember();
        member = memberRepository.save(member);
        coTrainer = TestFixture.createValidCoTrainer();
        var course = (Course) coTrainer.getBaseEvent();
        course.setMemberId(member.getId());
        space = TestFixture.createValidSpace(course);
        eventParticipant = TestFixture.createValidEventParticipant();
        var event = (Event) eventParticipant.getBaseEvent();
        event.setMemberId(member.getId());
        examParticipant = TestFixture.createValidExamParticipant();
        var exam = (Exam) examParticipant.getBaseEvent();
        exam.setMemberId(member.getId());
        exam.setMember(member);

        baseParticipantRepository.saveAll(Set.of(coTrainer, space, eventParticipant, examParticipant));
        var baseEvents = List.of(course, event, exam);
        baseEventRepository.saveAll(baseEvents);
        IntStream.range(0, baseEvents.size()).forEach(index -> {
            var localDate = LocalDate.now().plusDays(index);
            var calendarEntry = TestFixture.createValidCalendarEntry(baseEvents.get(index));
            calendarEntry.setEventFrom(LocalDateTime.of(localDate, LocalTime.MIN));
            calendarEntry.setEventTo(LocalDateTime.of(localDate, LocalTime.MAX));
            calendarList.add(calendarRepository.save(calendarEntry));
        });
    }

    @Test
    void getEntriesByPeriodAndEventTypes() throws Exception {
        TypeReference<List<Calendar>> typeReference = new TypeReference<>() {
        };
        var eventFrom = LocalDate.now().minusDays(1);
        var eventTo = LocalDate.now().plusWeeks(1);
        var mvcResult = mockMvc.perform(get(CALENDAR_URL_PREFIX)
                        .param("calendarEntriesFrom", eventFrom.toString())
                        .param("calendarEntriesTo", eventTo.toString()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, typeReference)).containsExactlyElementsOf(calendarList);
    }

    @Nested
    class GetCalendarEntry {

        private UUID calendarEntryId;

        @Test
        void course() throws Exception {
            setCalendarEntryId(Course.EVENT_TYPE);

            var courseDto = getBaseEventDto(CourseDto.class);

            assertEquals(member.getId(), courseDto.getMember().getId());
            assertThat(courseDto.getCalendarEntries()).singleElement().satisfies(this::assertCalendarEntry);
            assertThat(courseDto.getParticipants()).singleElement().satisfies(p -> assertParticipant(space, p));
            assertThat(courseDto.getCoTrainers()).singleElement().satisfies(p -> assertParticipant(coTrainer, p));
        }

        @Test
        void event() throws Exception {
            setCalendarEntryId(Event.EVENT_TYPE);

            var eventDto = getBaseEventDto(EventDto.class);

            assertEquals(member.getId(), eventDto.getMember().getId());
            assertThat(eventDto.getCalendarEntries()).singleElement().satisfies(this::assertCalendarEntry);
            assertThat(eventDto.getParticipants()).singleElement().satisfies(p -> assertParticipant(eventParticipant, p));
        }

        @Test
        void exam() throws Exception {
            setCalendarEntryId(Exam.EVENT_TYPE);

            var examDto = getBaseEventDto(ExamDto.class);

            assertEquals(member.getId(), examDto.getMember().getId());
            assertThat(examDto.getCalendarEntries()).singleElement().satisfies(this::assertCalendarEntry);
            assertThat(examDto.getParticipants()).singleElement().satisfies(p -> assertParticipant(examParticipant, p));
        }

        private void setCalendarEntryId(final String eventType) {
            calendarEntryId = calendarList.stream()
                    .filter(ce -> ce.getBaseEvent().getEventType().equals(eventType))
                    .findFirst()
                    .map(Calendar::getId)
                    .orElseThrow();
        }

        private <T> T getBaseEventDto(Class<T> clazz) throws Exception {
            var mvcResult = mockMvc.perform(get(CALENDAR_URL_PREFIX + "/{id}", calendarEntryId))
                    .andExpect(status().isOk())
                    .andReturn();

            return MvcMapper.toObject(mvcResult, clazz);
        }

        private void assertCalendarEntry(final Calendar ce) {
            assertEquals(calendarEntryId, ce.getId());
            assertNull(ce.getBaseEvent());
        }

        private void assertParticipant(final BaseParticipant expected, final BaseParticipant actual) {
            assertEquals(expected.getId(), actual.getId());
            assertNull(actual.getBaseEvent());
        }
    }
}
