package casp.web.backend.presentation.layer.event;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.types.ExamDto;
import casp.web.backend.business.logic.layer.event.types.ExamService;
import casp.web.backend.business.logic.layer.event.types.NewCalendarEntryDto;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.data.access.layer.event.types.ExamRepository;
import casp.web.backend.member.data.Member;
import casp.web.backend.member.data.MemberRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExamRestControllerTest {
    private static final String EXAM_URL_PREFIX = "/exam";
    private static final String EXAM_DOES_NOT_EXIST_MSG = "Exam with id %s does not exist or it is not active.";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;
    @Autowired
    private ExamRepository examRepository;

    @SpyBean
    private ExamService examService;
    private Exam exam;
    private LocalDateTime startDateTime;
    private Member member;

    @BeforeEach
    void setUp() {
        examRepository.deleteAll();
        memberRepository.deleteAll();

        member = memberRepository.save(TestFixture.createMember());
        exam = new Exam();
        exam.setName("exam");
        exam.setJudgeName("Judge");
        startDateTime = LocalDateTime.now();
        exam.addCalendarEntry(new CalendarEntry(startDateTime, startDateTime.plusHours(1)));
        memberReferenceRepository.findById(member.getId()).ifPresent(exam::setMember);
        exam = examRepository.save(exam);
    }

    @Test
    void migrateDataToV2() throws Exception {
        mockMvc.perform(post(EXAM_URL_PREFIX + "/migrate-data"))
                .andExpect(status().isNoContent());

        verify(examService).migrateDataToV2();
    }

    @Nested
    class DeleteById {
        @Test
        void isActive() throws Exception {
            performDelete()
                    .andExpect(status().isNoContent());

            verify(examService).deleteById(exam.getId());
        }

        @Test
        void isNotActive() throws Exception {
            exam.setEntityStatus(EntityStatus.INACTIVE);
            examRepository.save(exam);

            var exception = performDelete()
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo(EXAM_DOES_NOT_EXIST_MSG.formatted(exam.getId()));
        }

        private ResultActions performDelete() throws Exception {
            return mockMvc.perform(delete(EXAM_URL_PREFIX + "/{id}", exam.getId()));
        }
    }

    @Nested
    class Save {
        @Captor
        private ArgumentCaptor<ExamDto> examCaptor;
        private ExamWrite examWrite;

        @BeforeEach
        void setUp() {
            var newCalendarEntry = new NewCalendarEntryDto();
            newCalendarEntry.setEntryFrom(startDateTime.plusDays(1));
            newCalendarEntry.setEntryTo(startDateTime.plusDays(1).plusHours(1));
            examWrite = new ExamWrite();
            examWrite.setName("exam");
            examWrite.setNewMemberId(member.getId());
            examWrite.setNewCalendarEntry(newCalendarEntry);
            examWrite.setJudgeName("Judge");
        }

        @Test
        void badExam() throws Exception {
            var badExam = new ExamWrite();
            badExam.setParticipants(null);
            badExam.setNewParticipants(null);
            var exception = performPost(badExam)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            verifyNoInteractions(examService);
            assertThat(exception)
                    .isNotNull()
                    .message()
                    .contains("NotBlank.name",
                            "NotBlank.judgeName",
                            "A member is needed and the new member id cannot be the same as the actual member.",
                            "Whether it has a calendar entry or a recurrence option.");
        }

        @Test
        void memberDoesNotExist() throws Exception {
            examWrite.setNewMemberId(UUID.randomUUID());
            var exception = performPost(examWrite)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo("Member with id %s does not exist or it is not active.".formatted(examWrite.getNewMemberId()));
        }

        @Test
        void goodExam() throws Exception {
            performPost(examWrite)
                    .andExpect(status().isNoContent());

            verify(examService).save(examCaptor.capture());
            assertThat(examCaptor.getValue())
                    .satisfies(actualExam -> {
                        assertEquals(examWrite.getName(), actualExam.getName());
                        assertEquals(examWrite.getNewMemberId(), actualExam.getNewMemberId());
                        assertEquals(examWrite.getNewCalendarEntry().getEntryFrom(), actualExam.getNewCalendarEntry().getEntryFrom());
                        assertEquals(examWrite.getNewCalendarEntry().getEntryTo(), actualExam.getNewCalendarEntry().getEntryTo());
                    });
        }

        private ResultActions performPost(ExamWrite examWrite) throws Exception {
            return mockMvc.perform(post(EXAM_URL_PREFIX)
                    .content(MvcMapper.toString(examWrite))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetCalendarEntry {
        private UUID calendarEntryId;

        @BeforeEach
        void setUp() {
            calendarEntryId = exam.getCalendarEntries().getFirst().getId();
        }

        @Test
        void examDoesNotExist() throws Exception {
            var examId = UUID.randomUUID();
            var exception = performGet(examId, calendarEntryId)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo(EXAM_DOES_NOT_EXIST_MSG.formatted(examId));
        }

        @Test
        void calendarEntryDoesNotExist() throws Exception {
            var nonExistingCalendarEntry = UUID.randomUUID();
            var exception = performGet(exam.getId(), nonExistingCalendarEntry)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo("The Calendar entry with Id %s not found in Exam with Id %s".formatted(nonExistingCalendarEntry, exam.getId()));
        }

        @Test
        void calendarEntryExist() throws Exception {
            var mvcResult = performGet(exam.getId(), calendarEntryId)
                    .andExpect(status().isOk())
                    .andReturn();

            var examRead = MvcMapper.toObject(mvcResult, ExamRead.class);
            assertThat(examRead.getCalendarEntries())
                    .singleElement()
                    .satisfies(ce -> assertEquals(calendarEntryId, ce.getId()));
        }

        private ResultActions performGet(UUID examId, Object calendarEntryId) throws Exception {
            return mockMvc.perform(get(EXAM_URL_PREFIX + "/{examId}/calendar-entry/{calendarEntryId}", examId, calendarEntryId));
        }
    }
}
