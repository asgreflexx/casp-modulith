package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CourseFacadeImplTest {
    @Mock
    private CoTrainerService coTrainerService;
    @Mock
    private SpaceService spaceService;

    @InjectMocks
    private CourseFacadeImpl courseFacade;

    @Nested
    class MapBaseEventToDto {
        @Test
        void mapCoTrainer() {
            var coTrainer = TestFixture.createValidCoTrainer();
            var course = coTrainer.getBaseEvent();
            var member = course.getMember();
            coTrainer.setMemberOrHandlerId(member.getId());
            coTrainer.setMember(member);
            when(coTrainerService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(course.getId())).thenReturn(Set.of(coTrainer));

            var courseDto = courseFacade.mapBaseEventToDto(course);

            assertThat(courseDto.getCoTrainers())
                    .singleElement()
                    .satisfies(actual -> {
                        assertEquals(coTrainer.getId(), actual.getId());
                        assertEquals(coTrainer.getMemberOrHandlerId(), actual.getMember().getId());
                    });
        }

        @Test
        void mapSpace() {
            var dogHasHandler = TestFixture.createValidDogHasHandler();
            var space = TestFixture.createValidSpace();
            space.setMemberOrHandlerId(dogHasHandler.getId());
            var course = space.getBaseEvent();
            space.setDogHasHandler(dogHasHandler);
            when(spaceService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(course.getId())).thenReturn(Set.of(space));

            var courseDto = courseFacade.mapBaseEventToDto(course);

            assertThat(courseDto.getParticipants())
                    .singleElement()
                    .satisfies(actual -> {
                        assertEquals(space.getId(), actual.getId());
                        assertEquals(space.getMemberOrHandlerId(), actual.getDogHasHandler().getId());
                    });
        }
    }
}
