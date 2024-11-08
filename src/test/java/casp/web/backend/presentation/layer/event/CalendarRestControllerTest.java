package casp.web.backend.presentation.layer.event;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.types.CalendarEntryDto;
import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.CourseRepository;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.event.types.EventRepository;
import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.data.access.layer.event.types.ExamRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CalendarRestControllerTest {
    private static final String CALENDAR_URL_PREFIX = "/calendar";
    private static final TypeReference<List<CalendarEntryDto>> REFERENCE = new TypeReference<>() {
    };
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;
    private Course course;
    private Event event;
    private Exam exam;
    private LocalDate from;
    private LocalDate to;

    private static List<CalendarEntryDto> mapToCalendarEntries(MvcResult mvcResult) throws Exception {
        return MvcMapper.toObject(mvcResult, REFERENCE);
    }

    private static void assertCalendarEntry(CalendarEntryDto actual, BaseEvent expected) {
        var calendarEntry = expected.getCalendarEntries().getFirst();
        assertSame(expected.getEventType(), actual.getEventType());
        assertThat(actual.getEntryFrom())
                .isCloseTo(calendarEntry.getEntryFrom(), within(1, ChronoUnit.SECONDS));
        assertThat(actual.getEntryTo())
                .isCloseTo(calendarEntry.getEntryTo(), within(1, ChronoUnit.SECONDS));
    }

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        eventRepository.deleteAll();
        examRepository.deleteAll();
        memberRepository.deleteAll();

        var memberId = memberRepository.save(TestFixture.createMember()).getId();
        from = LocalDate.now();
        course = courseRepository.save(createBaseEvent(memberId, new Course(), from));
        event = eventRepository.save(createBaseEvent(memberId, new Event(), from.plusDays(1)));
        to = from.plusDays(2);
        exam = examRepository.save(createBaseEvent(memberId, new Exam(), to));
    }

    @Test
    void getCalendarEntries() throws Exception {
        var mvcResult = performGet(null)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mapToCalendarEntries(mvcResult))
                .zipSatisfy(List.of(course, event, exam), CalendarRestControllerTest::assertCalendarEntry);
    }

    @Test
    void getEventCalendarEntries() throws Exception {
        var mvcResult = performGet(Set.of(BaseEventType.EVENT))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mapToCalendarEntries(mvcResult))
                .singleElement()
                .satisfies(actual -> assertCalendarEntry(actual, event));
    }

    private <T extends BaseEvent> T createBaseEvent(UUID memberId, T baseEvent, LocalDate startTime) {
        baseEvent.setName(baseEvent.getEventType().name());
        baseEvent.addCalendarEntry(new CalendarEntry(startTime.atStartOfDay(), startTime.atTime(LocalTime.MAX)));
        memberReferenceRepository.findById(memberId).ifPresent(baseEvent::setMember);
        return baseEvent;
    }

    private ResultActions performGet(Set<BaseEventType> baseEventTypes) throws Exception {
        var requestBuilder = get(CALENDAR_URL_PREFIX)
                .param("from", from.toString())
                .param("to", to.toString());
        if (baseEventTypes != null) {
            baseEventTypes
                    .forEach(baseEventType -> requestBuilder.param("eventTypes", baseEventType.name()));
        }
        return mockMvc.perform(requestBuilder);
    }
}
