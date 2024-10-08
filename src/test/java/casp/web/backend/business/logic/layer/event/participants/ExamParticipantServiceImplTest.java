package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.event.types.Exam;
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
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamParticipantServiceImplTest {
    @Mock
    private BaseParticipantRepository baseParticipantRepository;
    @Mock
    private DogHasHandlerRepository dogHasHandlerRepository;

    @InjectMocks
    private ExamParticipantServiceImpl examParticipantService;
    private ExamParticipant participant;
    private Exam exam;
    private Set<ExamParticipant> expectedParticipants;
    private Set<BaseParticipant> baseParticipants;
    private String participantType;

    @BeforeEach
    void setUp() {
        participant = spy(TestFixture.createExamParticipant());
        participantType = participant.getParticipantType();
        exam = (Exam) participant.getBaseEvent();
        expectedParticipants = Set.of(participant);
        baseParticipants = expectedParticipants.stream()
                .map(BaseParticipant.class::cast)
                .collect(Collectors.toSet());
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

    private Optional<DogHasHandler> findDogHasHandler(final UUID id) {
        return dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(id, EntityStatus.ACTIVE);
    }

    @Nested
    class ReplaceParticipants {
        @Captor
        private ArgumentCaptor<Set<ExamParticipant>> captor;
        private Set<UUID> examParticipantsId;

        @BeforeEach
        void setUp() {
            var participant2 = TestFixture.createExamParticipant();
            participant.setBaseEvent(null);
            participant2.setBaseEvent(null);
            examParticipantsId = Set.of(participant, participant2).stream().map(ExamParticipant::getId).collect(Collectors.toSet());
            exam.setParticipantsSize(0);
        }

        @Test
        void deleteParticipants() {
            mockDogHasHandlers();

            examParticipantService.replaceParticipants(exam, examParticipantsId);

            verify(baseParticipantRepository).deleteAllByBaseEventIdAndParticipantType(exam.getId(), participantType);
        }

        @Test
        void setBaseEventToAllParticipantsAndSaveThem() {
            mockDogHasHandlers();

            examParticipantService.replaceParticipants(exam, examParticipantsId);

            verify(baseParticipantRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).allSatisfy(actual -> assertSame(exam, actual.getBaseEvent()));
        }

        @Test
        void setParticipantsSize() {
            mockDogHasHandlers();

            examParticipantService.replaceParticipants(exam, examParticipantsId);

            assertEquals(2, exam.getParticipantsSize());
        }

        @Test
        void setDogHasHandler() {
            var participantId = examParticipantsId.stream().findAny().orElseThrow();
            mockDogHasHandler(participantId);

            examParticipantService.replaceParticipants(exam, Set.of(participantId));

            verify(baseParticipantRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).allSatisfy(actual -> assertSame(participantId, actual.getDogHasHandler().getId()));
        }

        @Test
        void setMemberOrHandlerId() {
            var participantId = examParticipantsId.stream().findAny().orElseThrow();
            mockDogHasHandler(participantId);

            examParticipantService.replaceParticipants(exam, Set.of(participantId));

            verify(baseParticipantRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).allSatisfy(actual -> assertSame(participantId, actual.getMemberOrHandlerId()));
        }

        private void mockDogHasHandlers() {
            examParticipantsId.forEach(this::mockDogHasHandler);
        }

        private void mockDogHasHandler(final UUID id) {
            var dogHasHandler = TestFixture.createDogHasHandler();
            dogHasHandler.setId(id);
            when(findDogHasHandler(id)).thenReturn(Optional.of(dogHasHandler));
        }
    }

    @Nested
    class GetActiveExamParticipantsIfDogHasHandlersAreActive {
        @BeforeEach
        void setUp() {
            when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(exam.getId(), EntityStatus.ACTIVE, participantType)).thenReturn(baseParticipants);
        }

        @Test
        void spaceIsActive() {
            var dogHasHandler = TestFixture.createDogHasHandler();
            when(findDogHasHandler(participant.getMemberOrHandlerId())).thenReturn(Optional.of(dogHasHandler));

            assertThat(examParticipantService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(exam.getId()))
                    .singleElement()
                    .satisfies(pd -> assertEquals(dogHasHandler.getId(), pd.getDogHasHandler().getId()));
        }

        @Test
        void spaceIsInActive() {
            when(findDogHasHandler(participant.getMemberOrHandlerId())).thenReturn(Optional.empty());

            assertThat(examParticipantService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(exam.getId()))
                    .isEmpty();
        }
    }
}
