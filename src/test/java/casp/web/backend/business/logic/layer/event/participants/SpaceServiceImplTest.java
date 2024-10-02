package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.participants.SpaceRepository;
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
    private SpaceRepository spaceRepository;
    @Mock
    private DogHasHandlerRepository dogHasHandlerRepository;

    @InjectMocks
    private SpaceServiceImpl spaceService;
    private Space space;
    private Course course;
    private Set<Space> expectedSpaces;
    private String participantType;

    @BeforeEach
    void setUp() {
        space = spy(TestFixture.createSpace());
        participantType = space.getParticipantType();
        course = (Course) space.getBaseEvent();
        expectedSpaces = Set.of(space);
    }

    @Test
    void getParticipantsByBaseEvent() {
        when(spaceRepository.findAllByBaseEventIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(expectedSpaces);

        assertThat(spaceService.getParticipantsByBaseEventId(course.getId())).containsAll(expectedSpaces);
    }

    @Test
    void deactivateParticipantsByBaseEventId() {
        when(spaceRepository.findAllByBaseEventIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(expectedSpaces);

        spaceService.deactivateParticipantsByBaseEventId(course.getId());

        verify(space).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByBaseEventId() {
        when(spaceRepository.findAllByBaseEventIdAndEntityStatus(course.getId(), EntityStatus.INACTIVE)).thenReturn(expectedSpaces);

        spaceService.activateParticipantsByBaseEventId(course.getId());

        verify(space).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByBaseEventId() {
        when(spaceRepository.findAllByBaseEventIdAndEntityStatusNot(course.getId(), EntityStatus.DELETED)).thenReturn(expectedSpaces);

        spaceService.deleteParticipantsByBaseEventId(course.getId());

        verify(space).setEntityStatus(EntityStatus.DELETED);
    }

    @Test
    void deactivateParticipantsByMemberOrHandlerId() {
        when(spaceRepository.findAllByMemberOrHandlerIdAndEntityStatus(space.getMemberOrHandlerId(), EntityStatus.ACTIVE, participantType)).thenReturn(castToBaseParticipants());

        spaceService.deactivateParticipantsByMemberOrHandlerId(space.getMemberOrHandlerId());

        verify(space).setEntityStatus(EntityStatus.INACTIVE);
    }

    @Test
    void activateParticipantsByMemberOrHandlerId() {
        when(spaceRepository.findAllByMemberOrHandlerIdAndEntityStatus(space.getMemberOrHandlerId(), EntityStatus.INACTIVE, participantType)).thenReturn(castToBaseParticipants());

        spaceService.activateParticipantsByMemberOrHandlerId(space.getMemberOrHandlerId());

        verify(space).setEntityStatus(EntityStatus.ACTIVE);
    }

    @Test
    void deleteParticipantsByMemberOrHandlerId() {
        when(spaceRepository.findAllByMemberOrHandlerIdAndEntityStatusNot(space.getMemberOrHandlerId(), EntityStatus.DELETED, participantType)).thenReturn(castToBaseParticipants());

        spaceService.deleteParticipantsByMemberOrHandlerId(space.getMemberOrHandlerId());

        verify(space).setEntityStatus(EntityStatus.DELETED);
    }

    @Test
    void getSpacesByMemberId() {
        when(spaceRepository.findAllByMemberId(space.getMemberOrHandlerId())).thenReturn(expectedSpaces);

        var actualSpaces = spaceService.getSpacesByMemberId(space.getMemberOrHandlerId());

        assertThat(actualSpaces).containsAll(expectedSpaces);
    }

    @Test
    void getSpacesByDogId() {
        when(spaceRepository.findAllByDogId(space.getMemberOrHandlerId())).thenReturn(expectedSpaces);

        var actualSpaces = spaceService.getSpacesByDogId(space.getMemberOrHandlerId());

        assertThat(actualSpaces).containsAll(expectedSpaces);
    }

    private Set<BaseParticipant> castToBaseParticipants() {
        return expectedSpaces
                .stream()
                .map(BaseParticipant.class::cast)
                .collect(Collectors.toSet());
    }

    private Optional<DogHasHandler> findDogHasHandler(final UUID id) {
        return dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(id, EntityStatus.ACTIVE);
    }

    @Nested
    class SaveParticipant {
        @Test
        void saveSpace() {
            when(spaceRepository.save(space)).thenAnswer(invocation -> invocation.getArgument(0));

            assertThat(spaceService.saveParticipant(space)).isEqualTo(space);
        }

        @Test
        void setDogHasHandler() {
            var dogHasHandler = TestFixture.createDogHasHandler();
            when(dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(space.getMemberOrHandlerId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandler));

            spaceService.saveParticipant(space);

            verify(space).setDogHasHandler(dogHasHandler);
        }
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

            verify(spaceRepository).deleteAllByBaseEventId(course.getId());
        }

        @Test
        void setBaseEventToAllParticipantsAndSaveThem() {
            mockDogHasHandlers();

            spaceService.replaceParticipants(course, spacesId);

            verify(spaceRepository).saveAll(captor.capture());
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

            verify(spaceRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).allSatisfy(actual -> assertSame(participantId, actual.getDogHasHandler().getId()));
        }

        @Test
        void setMemberOrHandlerId() {
            var participantId = spacesId.stream().findAny().orElseThrow();
            mockDogHasHandler(participantId);

            spaceService.replaceParticipants(course, Set.of(participantId));

            verify(spaceRepository).saveAll(captor.capture());
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
            when(spaceRepository.findAllByBaseEventIdAndEntityStatus(course.getId(), EntityStatus.ACTIVE)).thenReturn(expectedSpaces);
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
