package casp.web.backend.business.logic.layer.events.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.participant.Space;
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
class SpaceServiceImplTest {
    @Mock
    private BaseParticipantRepository baseParticipantRepository;

    @InjectMocks
    private SpaceServiceImpl spaceService;
    private Space space;
    private Course course;
    private Set<Space> expectedSpaces;
    private Set<BaseParticipant> baseParticipants;
    private String participantType;

    @BeforeEach
    void setUp() {
        space = spy(TestFixture.createValidSpace());
        participantType = space.getParticipantType();
        course = (Course) space.getBaseEvent();
        expectedSpaces = Set.of(space);
        baseParticipants = expectedSpaces.stream()
                .map(BaseParticipant.class::cast)
                .collect(Collectors.toSet());
    }

    @Test
    void saveParticipants() {
        space.setBaseEvent(null);
        var newParticipants = Set.of(space);
        when(baseParticipantRepository.saveAll(expectedSpaces)).thenAnswer(invocation -> new ArrayList<>(expectedSpaces));

        assertThat(spaceService.saveParticipants(newParticipants, course)).containsAll(expectedSpaces);
        verify(baseParticipantRepository).deleteAllByBaseEventId(course.getId());
        verify(space).setBaseEvent(course);
    }

    @Test
    void getParticipantsByBaseEvent() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(baseParticipants);

        assertThat(spaceService.getParticipantsByBaseEventId(course.getId())).containsAll(expectedSpaces);
    }

    @Test
    void deactivateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(baseParticipants);

        spaceService.deactivateParticipantsByBaseEventId(course.getId());

        verify(space).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatus(course.getId(), EntityStatus.INACTIVE)).thenReturn(baseParticipants);

        spaceService.activateParticipantsByBaseEventId(course.getId());

        verify(space).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusNot(course.getId(), EntityStatus.DELETED)).thenReturn(baseParticipants);

        spaceService.deleteParticipantsByBaseEventId(course.getId());

        verify(space).setEntityStatus(EntityStatus.DELETED);
    }

    @Test
    void saveParticipant() {
        when(baseParticipantRepository.save(space)).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(spaceService.saveParticipant(space)).isEqualTo(space);
    }

    @Test
    void getSpacesByDogHasHandlersId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdIn(Set.of(space.getMemberOrHandlerId()), participantType)).thenReturn(baseParticipants);

        assertThat(spaceService.getSpacesByDogHasHandlersId(Set.of(space.getMemberOrHandlerId()))).containsAll(expectedSpaces);
    }

    @Test
    void deactivateParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(space.getMemberOrHandlerId(), EntityStatus.ACTIVE, participantType)).thenReturn(baseParticipants);

        spaceService.deactivateParticipantsByMemberOrHandlerId(space.getMemberOrHandlerId());

        verify(space).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(space.getMemberOrHandlerId(), EntityStatus.INACTIVE, participantType)).thenReturn(baseParticipants);

        spaceService.activateParticipantsByMemberOrHandlerId(space.getMemberOrHandlerId());

        verify(space).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatusNot(space.getMemberOrHandlerId(), EntityStatus.DELETED, participantType)).thenReturn(baseParticipants);

        spaceService.deleteParticipantsByMemberOrHandlerId(space.getMemberOrHandlerId());

        verify(space).setEntityStatus(EntityStatus.DELETED);
    }
}
