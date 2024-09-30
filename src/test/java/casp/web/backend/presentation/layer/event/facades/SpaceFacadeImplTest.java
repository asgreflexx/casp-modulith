package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.business.logic.layer.event.types.CourseService;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.presentation.layer.dtos.event.participants.SpaceReadDto;
import casp.web.backend.presentation.layer.dtos.event.participants.SpaceWriteDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SpaceFacadeImplTest {
    @Mock
    private SpaceService spaceService;
    @Mock
    private CourseService courseService;

    @InjectMocks
    private SpaceFacadeImpl spaceFacade;
    private Space space;

    @BeforeEach
    void setUp() {
        space = TestFixture.createSpace();
        var dogHasHandler = TestFixture.createDogHasHandler();
        space.setDogHasHandler(dogHasHandler);
        space.setMemberOrHandlerId(dogHasHandler.getId());

    }

    @Test
    void getSpacesByMemberId() {
        var memberId = UUID.randomUUID();
        when(spaceService.getSpacesByMemberId(memberId)).thenReturn(Set.of(space));

        var spacesDto = spaceFacade.getSpacesByMemberId(memberId);

        assertThat(spacesDto)
                .singleElement()
                .isInstanceOf(SpaceReadDto.class)
                .satisfies(this::assertSpaceToReadDto);
    }

    @Test
    void getSpacesByDogId() {
        var dogId = UUID.randomUUID();
        when(spaceService.getSpacesByDogId(dogId)).thenReturn(Set.of(space));

        var spacesDto = spaceFacade.getSpacesByDogId(dogId);

        assertThat(spacesDto)
                .singleElement()
                .isInstanceOf(SpaceReadDto.class)
                .satisfies(this::assertSpaceToReadDto);
    }

    @Test
    void save() {
        when(courseService.getOneById(space.getBaseEvent().getId())).thenReturn((Course) space.getBaseEvent());
        when(spaceService.saveParticipant(argThat(s -> space.getId() == s.getId()))).thenReturn(space);

        var spaceToReadDto = spaceFacade.save(toDto(space));

        assertThat(spaceToReadDto)
                .isInstanceOf(SpaceReadDto.class)
                .satisfies(this::assertSpaceToReadDto);
    }

    private void assertSpaceToReadDto(final SpaceReadDto spaceDto) {
        assertEquals(space.getId(), spaceDto.getId());
        assertEquals(space.getBaseEvent().getId(), spaceDto.getCourse().getId());
        assertEquals(space.getMemberOrHandlerId(), spaceDto.getMemberOrHandlerId());
        assertEquals(space.getMemberOrHandlerId(), spaceDto.getDogHasHandler().getId());
    }

    private SpaceWriteDto toDto(Space document) {
        var spaceToWriteDto = new SpaceWriteDto();
        spaceToWriteDto.setId(document.getId());
        spaceToWriteDto.setMemberOrHandlerId(document.getMemberOrHandlerId());
        spaceToWriteDto.setResponse(document.getResponse());
        spaceToWriteDto.setVersion(document.getVersion());
        spaceToWriteDto.setCreated(document.getCreated());
        spaceToWriteDto.setModified(document.getModified());
        spaceToWriteDto.setNote(document.getNote());
        spaceToWriteDto.setPaidPrice(document.getPaidPrice());
        spaceToWriteDto.setPaid(document.isPaid());
        spaceToWriteDto.setPaidDate(document.getPaidDate());
        spaceToWriteDto.setCourseId(document.getBaseEvent().getId());
        return spaceToWriteDto;
    }
}
