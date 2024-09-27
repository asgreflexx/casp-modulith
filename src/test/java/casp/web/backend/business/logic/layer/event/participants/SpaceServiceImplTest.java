package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.Course;
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
class SpaceServiceImplTest {
    @Mock
    private BaseParticipantRepository baseParticipantRepository;
    @Mock
    private DogHasHandlerRepository dogHasHandlerRepository;

    @InjectMocks
    private SpaceServiceImpl spaceService;
    private Space space;
    private Course course;
    private Set<Space> expectedSpaces;
    private Set<BaseParticipant> baseParticipants;
    private String participantType;

    @BeforeEach
    void setUp() {
        space = spy(TestFixture.createSpace());
        participantType = space.getParticipantType();
        course = (Course) space.getBaseEvent();
        expectedSpaces = Set.of(space);
        baseParticipants = expectedSpaces.stream()
                .map(BaseParticipant.class::cast)
                .collect(Collectors.toSet());
    }

    @Test
    void getParticipantsByBaseEvent() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(course.getId(), EntityStatus.ACTIVE, Space.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        assertThat(spaceService.getParticipantsByBaseEventId(course.getId())).containsAll(expectedSpaces);
    }

    @Test
    void deactivateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(course.getId(), EntityStatus.ACTIVE, Space.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        spaceService.deactivateParticipantsByBaseEventId(course.getId());

        verify(space).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(course.getId(), EntityStatus.INACTIVE, Space.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

        spaceService.activateParticipantsByBaseEventId(course.getId());

        verify(space).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByBaseEventId() {
        when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusNotAndParticipantType(course.getId(), EntityStatus.DELETED, Space.PARTICIPANT_TYPE)).thenReturn(baseParticipants);

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

    private Optional<DogHasHandler> findDogHasHandler(final UUID id) {
        return dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(id, EntityStatus.ACTIVE);
    }

    @Nested
    class ReplaceParticipants {
        @Captor
        private ArgumentCaptor<Set<Space>> captor;
        private Set<UUID> spacesId;

        @BeforeEach
        void setUp() {
            var space2 = TestFixture.createSpace();
            space.setBaseEvent(null);
            space2.setBaseEvent(null);
            spacesId = Set.of(space, space2).stream().map(BaseParticipant::getId).collect(Collectors.toSet());
            course.setParticipantsSize(0);
        }

        @Test
        void deleteParticipants() {
            mockDogHasHandlers();

            spaceService.replaceParticipants(course, spacesId);

            verify(baseParticipantRepository).deleteAllByBaseEventIdAndParticipantType(course.getId(), participantType);
        }

        @Test
        void setBaseEventToAllParticipantsAndSaveThem() {
            mockDogHasHandlers();

            spaceService.replaceParticipants(course, spacesId);

            verify(baseParticipantRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).allSatisfy(actual -> assertSame(course, actual.getBaseEvent()));
        }

        @Test
        void setParticipantsSize() {
            mockDogHasHandlers();

            spaceService.replaceParticipants(course, spacesId);

            assertEquals(2, course.getParticipantsSize());
        }

        @Test
        void setDogHasHandler() {
            var participantId = spacesId.stream().findAny().orElseThrow();
            mockDogHasHandler(participantId);

            spaceService.replaceParticipants(course, Set.of(participantId));

            verify(baseParticipantRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).allSatisfy(actual -> assertSame(participantId, actual.getDogHasHandler().getId()));
        }

        @Test
        void setMemberOrHandlerId() {
            var participantId = spacesId.stream().findAny().orElseThrow();
            mockDogHasHandler(participantId);

            spaceService.replaceParticipants(course, Set.of(participantId));

            verify(baseParticipantRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).allSatisfy(actual -> assertSame(participantId, actual.getMemberOrHandlerId()));
        }

        private void mockDogHasHandlers() {
            spacesId.forEach(this::mockDogHasHandler);
        }

        private void mockDogHasHandler(final UUID id) {
            var dogHasHandler = TestFixture.createDogHasHandler();
            dogHasHandler.setId(id);
            when(findDogHasHandler(id)).thenReturn(Optional.of(dogHasHandler));
        }
    }

    @Nested
    class GetActiveSpacesIfDogHasHandlersAreActive {
        @BeforeEach
        void setUp() {
            when(baseParticipantRepository.findAllByBaseEventIdAndEntityStatusAndParticipantType(course.getId(), EntityStatus.ACTIVE, Space.PARTICIPANT_TYPE)).thenReturn(baseParticipants);
        }

        @Test
        void spaceIsActive() {
            var dogHasHandler = TestFixture.createDogHasHandler();
            when(findDogHasHandler(space.getMemberOrHandlerId())).thenReturn(Optional.of(dogHasHandler));

            assertThat(spaceService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(course.getId()))
                    .singleElement()
                    .satisfies(pd -> assertEquals(dogHasHandler.getId(), pd.getDogHasHandler().getId()));
        }

        @Test
        void spaceIsInActive() {
            when(findDogHasHandler(space.getMemberOrHandlerId())).thenReturn(Optional.empty());

            assertThat(spaceService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(course.getId()))
                    .isEmpty();
        }
    }
}
