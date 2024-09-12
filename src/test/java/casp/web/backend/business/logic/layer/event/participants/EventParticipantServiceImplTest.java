package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventParticipantServiceImplTest {
    @Mock
    private BaseParticipantRepository baseParticipantRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private EventParticipantServiceImpl eventParticipantService;
    private EventParticipant participant;
    private Event event;
    private Set<EventParticipant> expectedParticipants;
    private Set<BaseParticipant> baseParticipants;
    private String participantType;

    @BeforeEach
    void setUp() {
        participant = spy(TestFixture.createValidEventParticipant());
        participantType = participant.getParticipantType();
        event = (Event) participant.getBaseEvent();
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

        assertThat(eventParticipantService.saveParticipants(newParticipants, event)).containsAll(expectedParticipants);
        verify(baseParticipantRepository).deleteAllByBaseEventId(event.getId());
        verify(participant).setBaseEvent(event);
    }

    @Test
    void getParticipantsByBaseEvent() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(event.getId(), EntityStatus.ACTIVE, EventParticipant.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        assertThat(eventParticipantService.getParticipantsByBaseEventId(event.getId())).containsAll(expectedParticipants);
    }

    @Test
    void deactivateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(event.getId(), EntityStatus.ACTIVE, EventParticipant.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        eventParticipantService.deactivateParticipantsByBaseEventId(event.getId());

        verify(participant).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(event.getId(), EntityStatus.INACTIVE, EventParticipant.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        eventParticipantService.activateParticipantsByBaseEventId(event.getId());

        verify(participant).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusNotAndParticipantType(event.getId(), EntityStatus.DELETED, EventParticipant.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        eventParticipantService.deleteParticipantsByBaseEventId(event.getId());

        verify(participant).setEntityStatus(EntityStatus.DELETED);
    }

    @Test
    void deactivateParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(participant.getMemberOrHandlerId(), EntityStatus.ACTIVE, participantType)).thenReturn(baseParticipants);

        eventParticipantService.deactivateParticipantsByMemberOrHandlerId(participant.getMemberOrHandlerId());

        verify(participant).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(participant.getMemberOrHandlerId(), EntityStatus.INACTIVE, participantType)).thenReturn(baseParticipants);

        eventParticipantService.activateParticipantsByMemberOrHandlerId(participant.getMemberOrHandlerId());

        verify(participant).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatusNot(participant.getMemberOrHandlerId(), EntityStatus.DELETED, participantType)).thenReturn(baseParticipants);

        eventParticipantService.deleteParticipantsByMemberOrHandlerId(participant.getMemberOrHandlerId());

        verify(participant).setEntityStatus(EntityStatus.DELETED);
    }

    @Nested
    class GetActiveEventParticipantsIfMembersAreActive {
        @BeforeEach
        void setUp() {
            when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(event.getId(), EntityStatus.ACTIVE, EventParticipant.PARTICIPANT_TYPE)).thenReturn(Set.of(participant));
        }

        @Test
        void coTrainerIsActive() {
            var member = TestFixture.createValidMember();
            when(memberRepository.findByIdAndEntityStatus(participant.getMemberOrHandlerId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            assertThat(eventParticipantService.getActiveEventParticipantsIfMembersAreActive(event.getId()))
                    .singleElement()
                    .satisfies(pm -> {
                        assertEquals(participant.getId(), pm.participant().getId());
                        assertEquals(member.getId(), pm.member().getId());
                    });
        }

        @Test
        void coTrainerIsInactiveActive() {
            when(memberRepository.findByIdAndEntityStatus(participant.getMemberOrHandlerId(), EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThat(eventParticipantService.getActiveEventParticipantsIfMembersAreActive(event.getId()))
                    .isEmpty();
        }
    }
}
