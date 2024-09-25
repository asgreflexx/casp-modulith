package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoTrainerServiceImplTest {
    @Mock
    private BaseParticipantRepository baseParticipantRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CoTrainerServiceImpl coTrainerService;
    private CoTrainer coTrainer;
    private Course course;
    private Set<CoTrainer> expectedCoTrainers;
    private Set<BaseParticipant> baseParticipants;
    private String participantType;

    @BeforeEach
    void setUp() {
        coTrainer = spy(TestFixture.createValidCoTrainer());
        participantType = coTrainer.getParticipantType();
        course = (Course) coTrainer.getBaseEvent();
        expectedCoTrainers = Set.of(coTrainer);
        baseParticipants = expectedCoTrainers.stream()
                .map(BaseParticipant.class::cast)
                .collect(Collectors.toSet());
    }

    @Test
    void getParticipantsByBaseEvent() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(course.getId(), EntityStatus.ACTIVE, CoTrainer.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        assertThat(coTrainerService.getParticipantsByBaseEventId(course.getId())).containsAll(expectedCoTrainers);
    }

    @Test
    void deactivateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(course.getId(), EntityStatus.ACTIVE, CoTrainer.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        coTrainerService.deactivateParticipantsByBaseEventId(course.getId());

        verify(coTrainer).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(course.getId(), EntityStatus.INACTIVE, CoTrainer.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        coTrainerService.activateParticipantsByBaseEventId(course.getId());

        verify(coTrainer).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusNotAndParticipantType(course.getId(), EntityStatus.DELETED, CoTrainer.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        coTrainerService.deleteParticipantsByBaseEventId(course.getId());

        verify(coTrainer).setEntityStatus(EntityStatus.DELETED);
    }

    @Test
    void deactivateParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(coTrainer.getMemberOrHandlerId(), EntityStatus.ACTIVE, participantType)).thenReturn(baseParticipants);

        coTrainerService.deactivateParticipantsByMemberOrHandlerId(coTrainer.getMemberOrHandlerId());

        verify(coTrainer).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatus(coTrainer.getMemberOrHandlerId(), EntityStatus.INACTIVE, participantType)).thenReturn(baseParticipants);

        coTrainerService.activateParticipantsByMemberOrHandlerId(coTrainer.getMemberOrHandlerId());

        verify(coTrainer).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByMemberOrHandlerId() {
        when(baseParticipantRepository.findAllByMemberOrHandlerIdAndEntityStatusNot(coTrainer.getMemberOrHandlerId(), EntityStatus.DELETED, participantType)).thenReturn(baseParticipants);

        coTrainerService.deleteParticipantsByMemberOrHandlerId(coTrainer.getMemberOrHandlerId());

        verify(coTrainer).setEntityStatus(EntityStatus.DELETED);
    }

    @Nested
    class ReplaceParticipants {
        @Captor
        private ArgumentCaptor<Set<CoTrainer>> captor;
        private Set<CoTrainer> coTrainers;

        @BeforeEach
        void setUp() {
            var coTrainer2 = TestFixture.createValidCoTrainer();
            coTrainer.setBaseEvent(null);
            coTrainer2.setBaseEvent(null);
            coTrainers = Set.of(coTrainer, coTrainer2);
            course.setParticipantsSize(0);
        }

        @Test
        void deleteParticipants() {
            coTrainerService.replaceParticipants(course, coTrainers);

            verify(baseParticipantRepository).deleteAllByBaseEventId(course.getId());
        }

        @Test
        void setBaseEventToAllParticipantsAndSaveThem() {
            coTrainerService.replaceParticipants(course, coTrainers);

            verify(baseParticipantRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).allSatisfy(actual -> assertSame(course, actual.getBaseEvent()));
        }

        @Test
        void setParticipantsSize() {
            coTrainerService.replaceParticipants(course, coTrainers);

            assertEquals(2, course.getParticipantsSize());
        }
    }

    @Nested
    class GetActiveCoTrainersAndDeleteTheOthers {
        @BeforeEach
        void setUp() {
            when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(course.getId(), EntityStatus.ACTIVE, CoTrainer.PARTICIPANT_TYPE)).thenReturn(Set.of(coTrainer));
        }

        @Test
        void coTrainerIsActive() {
            var member = TestFixture.createValidMember();
            when(memberRepository.findByIdAndEntityStatus(coTrainer.getMemberOrHandlerId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));

            assertThat(coTrainerService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(course.getId()))
                    .singleElement()
                    .satisfies(pm -> assertEquals(member.getId(), pm.getMember().getId()));
        }

        @Test
        void coTrainerIsInactiveActive() {
            when(memberRepository.findByIdAndEntityStatus(coTrainer.getMemberOrHandlerId(), EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThat(coTrainerService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(course.getId()))
                    .isEmpty();
        }
    }
}
