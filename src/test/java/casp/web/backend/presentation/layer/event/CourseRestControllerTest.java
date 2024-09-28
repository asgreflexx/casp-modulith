package casp.web.backend.presentation.layer.event;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.event.calendar.CalendarRepository;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static casp.web.backend.presentation.layer.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.CourseMapper.COURSE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseRestControllerTest {
    private static final String COURSE_URL_PREFIX = "/course";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BaseEventRepository baseEventRepository;
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;

    private DogHasHandler dogHasHandler;

    @BeforeEach
    void setUp() {
        calendarRepository.deleteAll();
        baseParticipantRepository.deleteAll();
        baseEventRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();
        memberRepository.deleteAll();
        dogRepository.deleteAll();

        dogHasHandler = TestFixture.createDogHasHandler();
        memberRepository.save(dogHasHandler.getMember());
        dogRepository.save(dogHasHandler.getDog());
        dogHasHandlerRepository.save(dogHasHandler);
    }

    @Nested
    class Save {

        @Test
        void invalid() throws Exception {
            var mvcResult = performPost(new CourseDto())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertThat(mvcResult.getResolvedException())
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage()).contains("NotEmpty.calendarEntries", "NotNull.memberId", "NotBlank.name"));
        }

        @Test
        void valid() throws Exception {
            var course = TestFixture.createCourse();
            var courseDto = COURSE_MAPPER.toDto(course);
            courseDto.setMemberId(dogHasHandler.getMemberId());
            courseDto.setMember(null);
            courseDto.setCalendarEntries(CALENDAR_MAPPER.toDtoList(List.of(TestFixture.createCalendarEntry())));
            performPost(courseDto)
                    .andExpect(status().isNoContent());

            assertThat(baseEventRepository.findAll())
                    .singleElement()
                    .satisfies(c -> assertEquals(courseDto.getMemberId(), c.getMember().getId()));
        }

        private ResultActions performPost(final CourseDto courseDto) throws Exception {
            return mockMvc.perform(post(COURSE_URL_PREFIX)
                    .content(MvcMapper.toString(courseDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }
}
