package casp.web.backend.business.logic.layer.events.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamParticipantServiceImplTest {
    @Mock
    private BaseParticipantRepository baseParticipantRepository;

    @InjectMocks
    private ExamParticipantServiceImpl examParticipantService;
    private ExamParticipant participant;
    private Exam exam;
    private Set<ExamParticipant> expectedParticipants;
    private Set<BaseParticipant> baseParticipants;
    private String participantType;

    @BeforeEach
    void setUp() {
        participant = spy(TestFixture.createValidExamParticipant());
        participantType = participant.getParticipantType();
        exam = (Exam) participant.getBaseEvent();
        expectedParticipants = Set.of(participant);
        baseParticipants = expectedParticipants.stream()
                .map(BaseParticipant.class::cast)
                .collect(Collectors.toSet());
    }

    @Test
    void saveParticipants() {
        participant.setBaseEvent(null);
        var newParticipants = Set.of(participant);
        when(baseParticipantRepository.saveAll(expectedParticipants)).thenAnswer(invocation -> new ArrayList<>(expectedParticipants));

        assertThat(examParticipantService.saveParticipants(newParticipants, exam)).containsAll(expectedParticipants);
        verify(baseParticipantRepository).deleteAllByBaseEventId(exam.getId());
        verify(participant).setBaseEvent(exam);
    }

    @Test
    void getParticipantsByBaseEvent() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(exam.getId(), EntityStatus.ACTIVE, ExamParticipant.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        assertThat(examParticipantService.getParticipantsByBaseEventId(exam.getId())).containsAll(expectedParticipants);
    }

    @Test
    void deactivateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(exam.getId(), EntityStatus.ACTIVE, ExamParticipant.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        examParticipantService.deactivateParticipantsByBaseEventId(exam.getId());

        verify(participant).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(exam.getId(), EntityStatus.INACTIVE, ExamParticipant.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        examParticipantService.activateParticipantsByBaseEventId(exam.getId());

        verify(participant).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusNotAndParticipantType(exam.getId(), EntityStatus.DELETED, ExamParticipant.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        examParticipantService.deleteParticipantsByBaseEventId(exam.getId());

        verify(participant).setEntityStatus(EntityStatus.DELETED);
    }

    @Test
    void deactivateParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(participant.getMemberOrHandlerId(), EntityStatus.ACTIVE, participantType)).thenReturn(baseParticipants);

        examParticipantService.deactivateParticipantsByMemberOrHandlerId(participant.getMemberOrHandlerId());

        verify(participant).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(participant.getMemberOrHandlerId(), EntityStatus.INACTIVE, participantType)).thenReturn(baseParticipants);

        examParticipantService.activateParticipantsByMemberOrHandlerId(participant.getMemberOrHandlerId());

        verify(participant).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatusNot(participant.getMemberOrHandlerId(), EntityStatus.DELETED, participantType)).thenReturn(baseParticipants);

        examParticipantService.deleteParticipantsByMemberOrHandlerId(participant.getMemberOrHandlerId());

        verify(participant).setEntityStatus(EntityStatus.DELETED);
    }
}
