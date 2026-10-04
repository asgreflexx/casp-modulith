package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.ExamParticipant;
import casp.web.backend.common.enums.EntityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ExamParticipant active;

    private Exam exam;

    @BeforeEach
    void setUp() {
        exam = new Exam();
        when(active.getDogHasHandler().getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
        when(active.getDogHasHandler().getMember().getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
        when(active.getDogHasHandler().getDog().getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
    }

    @ParameterizedTest
    @MethodSource
    void getNotDeletedParticipants(DeletedParticipant deletedParticipant) {
        var deleted = mock(ExamParticipant.class, Answers.RETURNS_DEEP_STUBS);
        when(deleted.getDogHasHandler().getEntityStatus()).thenReturn(deletedParticipant.dogHasHandlerStatus());
        when(deleted.getDogHasHandler().getMember().getEntityStatus()).thenReturn(deletedParticipant.memberStatus());
        when(deleted.getDogHasHandler().getDog().getEntityStatus()).thenReturn(deletedParticipant.dogStatus());

        var participants = Set.of(active, deleted);
        exam.setParticipants(participants);

        assertThat(exam.getParticipants())
                .singleElement()
                .isEqualTo(active);
    }

    @SuppressWarnings("java:S1144") // False positive
    private static Stream<DeletedParticipant> getNotDeletedParticipants() {
        return Stream.of(new DeletedParticipant(EntityStatus.DELETED, EntityStatus.ACTIVE, EntityStatus.ACTIVE),
                new DeletedParticipant(EntityStatus.ACTIVE, EntityStatus.DELETED, EntityStatus.ACTIVE),
                new DeletedParticipant(EntityStatus.ACTIVE, EntityStatus.ACTIVE, EntityStatus.DELETED));
    }

    private record DeletedParticipant(
            EntityStatus dogHasHandlerStatus,
            EntityStatus memberStatus,
            EntityStatus dogStatus
    ) {
    }
}
