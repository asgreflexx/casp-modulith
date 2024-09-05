package casp.web.backend.business.logic.layer.events.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.participant.CoTrainer;
import casp.web.backend.data.access.layer.documents.event.types.Course;
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
class CoTrainerServiceImplTest {
    @Mock
    private BaseParticipantRepository baseParticipantRepository;

    @InjectMocks
    private CoTrainerServiceImpl coTrainerService;
    private CoTrainer coTrainer;
    private Course course;
    private Set<CoTrainer> expectedCoTrainers;
    private Set<BaseParticipant> baseParticipants;

    @BeforeEach
    void setUp() {
        coTrainer = spy(TestFixture.createValidCoTrainer());
        course = (Course) coTrainer.getBaseEvent();
        expectedCoTrainers = Set.of(coTrainer);
        baseParticipants = expectedCoTrainers.stream()
                .map(BaseParticipant.class::cast)
                .collect(Collectors.toSet());
    }

    @Test
    void saveParticipants() {
        coTrainer.setBaseEvent(null);
        var newParticipants = Set.of(coTrainer);
        when(baseParticipantRepository.saveAll(expectedCoTrainers)).thenAnswer(invocation -> new ArrayList<>(expectedCoTrainers));

        assertThat(coTrainerService.saveParticipants(newParticipants, course)).containsAll(expectedCoTrainers);
        verify(baseParticipantRepository).deleteAllByBaseEventId(course.getId());
        verify(coTrainer).setBaseEvent(course);
    }

    @Test
    void getParticipantsByBaseEvent() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(baseParticipants);

        assertThat(coTrainerService.getParticipantsByBaseEventId(course.getId())).containsAll(expectedCoTrainers);
    }

    @Test
    void deactivateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(baseParticipants);

        coTrainerService.deactivateParticipantsByBaseEventId(course.getId());

        verify(coTrainer).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatus(course.getId(), EntityStatus.INACTIVE)).thenReturn(baseParticipants);

        coTrainerService.activateParticipantsByBaseEventId(course.getId());

        verify(coTrainer).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusNot(course.getId(), EntityStatus.DELETED)).thenReturn(baseParticipants);

        coTrainerService.deleteParticipantsByBaseEventId(course.getId());

        verify(coTrainer).setEntityStatus(EntityStatus.DELETED);
    }

    @Test
    void deactivateParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(coTrainer.getMemberOrHandlerId(), EntityStatus.ACTIVE)).thenReturn(baseParticipants);

        coTrainerService.deactivateParticipantsByMemberOrHandlerId(coTrainer.getMemberOrHandlerId());

        verify(coTrainer).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(coTrainer.getMemberOrHandlerId(), EntityStatus.INACTIVE)).thenReturn(baseParticipants);

        coTrainerService.activateParticipantsByMemberOrHandlerId(coTrainer.getMemberOrHandlerId());

        verify(coTrainer).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatusNot(coTrainer.getMemberOrHandlerId(), EntityStatus.DELETED)).thenReturn(baseParticipants);

        coTrainerService.deleteParticipantsByMemberOrHandlerId(coTrainer.getMemberOrHandlerId());

        verify(coTrainer).setEntityStatus(EntityStatus.DELETED);
    }
}
