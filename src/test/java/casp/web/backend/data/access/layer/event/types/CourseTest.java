package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Space activeSpace;

    private Course course;

    private static CoTrainer mockCotrainer(final EntityStatus entityStatus) {
        var coTrainer = mock(CoTrainer.class, Answers.RETURNS_DEEP_STUBS);
        when(coTrainer.getMember().getEntityStatus()).thenReturn(entityStatus);
        return coTrainer;
    }

    private static Stream<DeletedSpace> getNotDeletedSpaces() {
        return Stream.of(new DeletedSpace(EntityStatus.DELETED, EntityStatus.ACTIVE, EntityStatus.ACTIVE),
                new DeletedSpace(EntityStatus.ACTIVE, EntityStatus.DELETED, EntityStatus.ACTIVE),
                new DeletedSpace(EntityStatus.ACTIVE, EntityStatus.ACTIVE, EntityStatus.DELETED));
    }

    @BeforeEach
    void setUp() {
        course = new Course();
    }

    @Test
    void getNotDeletedCoTrainers() {
        var activeCoTrainer = mockCotrainer(EntityStatus.ACTIVE);
        var deletedCoTrainer = mockCotrainer(EntityStatus.DELETED);
        course.setCoTrainers(Set.of(activeCoTrainer, deletedCoTrainer));

        assertThat(course.getCoTrainers())
                .singleElement()
                .isEqualTo(activeCoTrainer);
    }

    @ParameterizedTest
    @MethodSource
    void getNotDeletedSpaces(DeletedSpace deletedSpace) {
        var spaceSet = mockSpaceSet(deletedSpace);
        course.setSpaces(spaceSet);

        assertThat(course.getSpaces())
                .singleElement()
                .isEqualTo(activeSpace);
    }

    @ParameterizedTest
    @MethodSource("getNotDeletedSpaces")
    void getSpaceListSize(DeletedSpace deletedSpace) {
        var spaceSet = mockSpaceSet(deletedSpace);
        course.setSpaces(spaceSet);

        assertEquals(1, course.getSpaceListSize());
    }

    private Set<Space> mockSpaceSet(final DeletedSpace deletedSpace) {
        when(activeSpace.getDogHasHandler().getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
        when(activeSpace.getDogHasHandler().getMember().getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
        when(activeSpace.getDogHasHandler().getDog().getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
        var deleted = mock(Space.class, Answers.RETURNS_DEEP_STUBS);
        when(deleted.getDogHasHandler().getEntityStatus()).thenReturn(deletedSpace.dogHasHandlerStatus());
        when(deleted.getDogHasHandler().getMember().getEntityStatus()).thenReturn(deletedSpace.memberStatus());
        when(deleted.getDogHasHandler().getDog().getEntityStatus()).thenReturn(deletedSpace.dogStatus());
        return Set.of(activeSpace, deleted);
    }

    private record DeletedSpace(
            EntityStatus dogHasHandlerStatus,
            EntityStatus memberStatus,
            EntityStatus dogStatus
    ) {
    }
}
