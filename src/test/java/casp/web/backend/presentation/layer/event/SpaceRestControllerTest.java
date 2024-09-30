package casp.web.backend.presentation.layer.event;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.dtos.event.participants.SpaceReadDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SpaceRestControllerTest {
    private static final String SPACE_URL_PREFIX = "/space";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BaseEventRepository baseEventRepository;
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    private Space space;

    @BeforeEach
    void setUp() {
        baseParticipantRepository.deleteAll();
        baseEventRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();

        var dogHasHandler = TestFixture.createDogHasHandler();
        space = TestFixture.createSpace();
        space.setDogHasHandler(dogHasHandler);
        space.setMemberOrHandlerId(dogHasHandler.getId());

        baseParticipantRepository.save(space);
        baseEventRepository.save(space.getBaseEvent());
        dogHasHandlerRepository.save(dogHasHandler);
    }

    @Test
    void getSpacesByMemberId() throws Exception {
        var spaceReadDtoSet = getSpacesBy("/by-member-id/{memberId}", space.getDogHasHandler().getMemberId());

        assertThat(spaceReadDtoSet)
                .singleElement()
                .satisfies(this::assertSpaceToReadDto);
    }

    private Set<SpaceReadDto> getSpacesBy(final String postfix, final UUID id) throws Exception {
        TypeReference<Set<SpaceReadDto>> typeReference = new TypeReference<>() {
        };
        var mvcResult = mockMvc.perform(get(SPACE_URL_PREFIX + postfix, id))
                .andExpect(status().isOk())
                .andReturn();
        return MvcMapper.toObject(mvcResult, typeReference);
    }

    private void assertSpaceToReadDto(final SpaceReadDto spaceDto) {
        assertEquals(space.getId(), spaceDto.getId());
        assertEquals(space.getBaseEvent().getId(), spaceDto.getCourse().getId());
        assertEquals(space.getMemberOrHandlerId(), spaceDto.getMemberOrHandlerId());
        assertEquals(space.getMemberOrHandlerId(), spaceDto.getDogHasHandler().getId());
    }
}
