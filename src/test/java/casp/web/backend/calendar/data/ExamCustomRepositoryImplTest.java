package casp.web.backend.calendar.data;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ExamCustomRepositoryImplTest {
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;
    private CalendarEntry calendarEntry;

    @BeforeEach
    void setUp() {
        examRepository.deleteAll();
        dogHasHandlerReferenceRepository.deleteAll();
        memberReferenceRepository.deleteAll();
    }

    @Nested
    class FindAllByParticipantId {

        private ExamParticipant participant;

        @BeforeEach
        void setUp() {
            var dogHasHandlerReference = dogHasHandlerReferenceRepository.save(ReferenceTestFixture.createDogHasHandlerReference());
            participant = new ExamParticipant(dogHasHandlerReference);
        }

        @Test
        void multipleExams() {
            var exam1 = createExam("Exam1", EntityStatus.ACTIVE, participant);
            var exam2 = createExam("Exam2", EntityStatus.ACTIVE, participant);

            var examPage = examRepository.findAllByParticipant(participant, Pageable.unpaged());

            assertThat(examPage)
                    .containsExactlyInAnyOrder(exam1, exam2);
        }

        @Test
        void oneExamIsNotActive() {
            var exam1 = createExam("Exam1", EntityStatus.ACTIVE, participant);
            createExam("Exam2", EntityStatus.INACTIVE, participant);

            var examPage = examRepository.findAllByParticipant(participant, Pageable.unpaged());

            assertThat(examPage)
                    .containsExactlyInAnyOrder(exam1);
        }
    }

    @Nested
    class FindAllByMemberId {
        private Exam exam1;
        private Exam exam2;
        private ExamParticipant participant2;


        @BeforeEach
        void setUp() {
            var participant1 = new ExamParticipant(dogHasHandlerReferenceRepository.save(ReferenceTestFixture.createDogHasHandlerReference()));
            exam1 = examRepository.save(createExam("Exam1", EntityStatus.ACTIVE, participant1));
            participant2 = new ExamParticipant(dogHasHandlerReferenceRepository.save(ReferenceTestFixture.createDogHasHandlerReference()));
            exam2 = examRepository.save(createExam("Exam2", EntityStatus.ACTIVE, participant2));
        }

        @Test
        void memberIdIsNull() {
            var actualExams = examRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo(), null);

            assertThat(actualExams).containsExactlyInAnyOrder(exam1, exam2);
        }

        @Test
        void memberIdIsTheSameAsExamMember() {
            var actualExams = examRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo(), exam2.member.getId());

            assertThat(actualExams).containsExactly(exam2);
        }

        @Test
        void memberIdIsTheSameAsExamParticipant() {
            var actualExams = examRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo(), participant2.getDogHasHandler().getMember().getId());

            assertThat(actualExams).containsExactly(exam2);
        }
    }

    private Exam createExam(String name, EntityStatus entityStatus, ExamParticipant participant) {
        calendarEntry = new CalendarEntry();
        calendarEntry.setEntryFrom(LocalDateTime.of(2024, 1, 1, 0, 0));
        calendarEntry.setEntryTo(calendarEntry.getEntryFrom().plusHours(10));
        var exam = new Exam();
        exam.setName(name);
        exam.setEntityStatus(entityStatus);
        exam.setMember(ReferenceTestFixture.createMemberReference());
        exam.addCalendarEntry(calendarEntry);
        exam.addParticipants(Set.of(participant));
        return examRepository.save(exam);
    }
}