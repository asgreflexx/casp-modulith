package casp.web.backend.calendar.data;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static casp.web.backend.calendar.CalendarFixture.createCalendarEntry;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class ExamCustomRepositoryImplTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;

    @BeforeEach
    void setUp() {
        examRepository.deleteAll();
        dogHasHandlerReferenceRepository.deleteAll();
        memberReferenceRepository.deleteAll();
    }

    @Nested
    class FindAllByParticipantId {

        private ExamParticipant participant;
        private DogHasHandlerReference dogHasHandlerReference;

        @BeforeEach
        void setUp() {
            dogHasHandlerReference = dogHasHandlerReferenceRepository.save(ReferenceTestFixture.createDogHasHandlerReference());
            participant = new ExamParticipant(dogHasHandlerReference);
        }

        @Test
        void multipleExams() {
            var exam1 = createExam("Exam1", EntityStatus.ACTIVE, participant);
            var exam2 = createExam("Exam2", EntityStatus.ACTIVE, participant);

            var examPage = examRepository.findAllByParticipantId(participant.getId(), Pageable.unpaged());

            assertThat(examPage)
                    .containsExactlyInAnyOrder(exam1, exam2);
        }

        @Test
        void oneExamIsNotActive() {
            var exam1 = createExam("Exam1", EntityStatus.ACTIVE, participant);
            createExam("Exam2", EntityStatus.INACTIVE, participant);

            var examPage = examRepository.findAllByParticipantId(participant.getId(), Pageable.unpaged());

            assertThat(examPage)
                    .containsExactlyInAnyOrder(exam1);
        }

        @Test
        void participantIsNotActive() {
            dogHasHandlerReference.setEntityStatus(EntityStatus.INACTIVE);
            dogHasHandlerReferenceRepository.save(dogHasHandlerReference);
            createExam("Exam1", EntityStatus.ACTIVE, participant);

            var examPage = examRepository.findAllByParticipantId(participant.getId(), Pageable.unpaged());

            assertThat(examPage).isEmpty();
        }
    }

    private Exam createExam(String name, EntityStatus entityStatus, ExamParticipant participant) {
        var calendarEntry = createCalendarEntry();
        var exam = new Exam();
        exam.setName(name);
        exam.setEntityStatus(entityStatus);
        exam.setMember(ReferenceTestFixture.createMemberReference());
        exam.addCalendarEntry(calendarEntry);
        exam.addParticipants(Set.of(participant));
        return examRepository.save(exam);
    }
}
