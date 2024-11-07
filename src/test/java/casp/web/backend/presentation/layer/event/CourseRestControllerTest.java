package casp.web.backend.presentation.layer.event;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.types.CourseDto;
import casp.web.backend.business.logic.layer.event.types.CourseService;
import casp.web.backend.business.logic.layer.event.types.NewCalendarEntryDto;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.DogReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.CourseRepository;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.RestResponsePage;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseRestControllerTest {
    private static final String COURSE_URL_PREFIX = "/course";
    private static final String COURSE_DOES_NOT_EXIST_MSG = "Course with id %s does not exist or it is not active.";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private DogReferenceRepository dogReferenceRepository;
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    @SpyBean
    private CourseService courseService;

    private Course course;
    private DogHasHandler dogHasHandler;
    private Member member;
    private LocalDateTime startDateTime;


    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();
        dogRepository.deleteAll();
        memberRepository.deleteAll();

        member = memberRepository.save(TestFixture.createMember());
        var dog = dogRepository.save(TestFixture.createDog());
        dogHasHandler = new DogHasHandler();
        memberReferenceRepository.findById(member.getId()).ifPresent(dogHasHandler::setMember);
        dogReferenceRepository.findById(dog.getId()).ifPresent(dogHasHandler::setDog);
        dogHasHandler = dogHasHandlerRepository.save(dogHasHandler);
        course = new Course();
        course.setName("course");
        startDateTime = LocalDateTime.now();
        course.addCalendarEntry(new CalendarEntry(startDateTime, startDateTime.plusHours(1)));
        memberReferenceRepository.findById(member.getId()).ifPresent(course::setMember);
        course = courseRepository.save(course);
    }

    @Nested
    class Save {
        @Captor
        private ArgumentCaptor<CourseDto> courseCaptor;
        private CourseWrite courseWrite;

        @BeforeEach
        void setUp() {
            var newCalendarEntry = new NewCalendarEntryDto();
            newCalendarEntry.setEntryFrom(startDateTime.plusDays(1));
            newCalendarEntry.setEntryTo(startDateTime.plusDays(1).plusHours(1));
            courseWrite = new CourseWrite();
            courseWrite.setName("course");
            courseWrite.setNewMemberId(member.getId());
            courseWrite.setNewCalendarEntry(newCalendarEntry);
        }

        @Test
        void badCourse() throws Exception {
            var badCourse = new CourseWrite();
            badCourse.setSpaces(null);
            badCourse.setNewSpaces(null);
            badCourse.setCoTrainers(null);
            badCourse.setNewCoTrainers(null);
            var exception = performPost(badCourse)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            verifyNoInteractions(courseService);
            assertThat(exception)
                    .isNotNull()
                    .message()
                    .contains("NotBlank.name",
                            "A member is needed and the new member id cannot be the same as the actual member.",
                            "Whether it has a calendar entry or a recurrence option.");
        }

        @Test
        void memberDoesNotExist() throws Exception {
            courseWrite.setNewMemberId(UUID.randomUUID());
            var exception = performPost(courseWrite)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo("Member with id %s does not exist or it is not active.".formatted(courseWrite.getNewMemberId()));
        }

        @Test
        void goodCourse() throws Exception {
            performPost(courseWrite)
                    .andExpect(status().isNoContent());

            verify(courseService).save(courseCaptor.capture());
            assertThat(courseCaptor.getValue())
                    .satisfies(actualCourse -> {
                        assertEquals(courseWrite.getName(), actualCourse.getName());
                        assertEquals(courseWrite.getNewMemberId(), actualCourse.getNewMemberId());
                        assertEquals(courseWrite.getNewCalendarEntry().getEntryFrom(), actualCourse.getNewCalendarEntry().getEntryFrom());
                        assertEquals(courseWrite.getNewCalendarEntry().getEntryTo(), actualCourse.getNewCalendarEntry().getEntryTo());
                    });
        }

        private ResultActions performPost(CourseWrite courseWrite) throws Exception {
            return mockMvc.perform(post(COURSE_URL_PREFIX)
                    .content(MvcMapper.toString(courseWrite))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetAllByYear {
        @ParameterizedTest
        @NullSource
        @ValueSource(ints = {-1, 0})
        void badYear(Integer year) throws Exception {
            performGet(year)
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(courseService);
        }

        @Test
        void getCourse() throws Exception {
            var mvcResult = performGet(startDateTime.getYear())
                    .andExpect(status().isOk())
                    .andReturn();
            TypeReference<RestResponsePage<CourseRead>> coursePage = new TypeReference<>() {
            };
            assertThat(MvcMapper.toObject(mvcResult, coursePage))
                    .singleElement()
                    .satisfies(actualCourse -> assertEquals(course.getId(), actualCourse.getId()));
        }

        private ResultActions performGet(Integer year) throws Exception {
            var requestBuilder = get(COURSE_URL_PREFIX);
            if (null != year) {
                requestBuilder.param("year", year.toString());
            }
            return mockMvc.perform(requestBuilder);
        }
    }

    @Nested
    class GetSpacesEmail {
        @Test
        void isActive() throws Exception {
            dogHasHandlerReferenceRepository.findById(dogHasHandler.getId()).ifPresent(dhh -> {
                course.setSpaceLimit(1);
                course.addSpace(new Space(dhh));
                courseRepository.save(course);
            });
            var mvcResult = performGet()
                    .andExpect(status().isOk())
                    .andReturn();
            TypeReference<Set<String>> emails = new TypeReference<>() {
            };
            assertThat(MvcMapper.toObject(mvcResult, emails))
                    .containsOnly(member.getEmail());
        }

        @Test
        void isNotActive() throws Exception {
            course.setEntityStatus(EntityStatus.INACTIVE);
            courseRepository.save(course);

            var exception = performGet()
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo(COURSE_DOES_NOT_EXIST_MSG.formatted(course.getId()));
        }

        private ResultActions performGet() throws Exception {
            return mockMvc.perform(get(COURSE_URL_PREFIX + "/emails/{id}", course.getId()));
        }
    }

    @Nested
    class DeleteById {
        @Test
        void isActive() throws Exception {
            performDelete()
                    .andExpect(status().isNoContent());

            verify(courseService).deleteById(course.getId());
        }

        @Test
        void isNotActive() throws Exception {
            course.setEntityStatus(EntityStatus.INACTIVE);
            courseRepository.save(course);

            var exception = performDelete()
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo(COURSE_DOES_NOT_EXIST_MSG.formatted(course.getId()));
        }

        private ResultActions performDelete() throws Exception {
            return mockMvc.perform(delete(COURSE_URL_PREFIX + "/{id}", course.getId()));
        }
    }

    @Nested
    class GetOneById {
        @Test
        void isActive() throws Exception {
            var mvcResult = performGet()
                    .andExpect(status().isOk())
                    .andReturn();

            var courseRead = MvcMapper.toObject(mvcResult, CourseRead.class);
            assertEquals(course.getId(), courseRead.getId());
        }

        @Test
        void isNotActive() throws Exception {
            course.setEntityStatus(EntityStatus.INACTIVE);
            courseRepository.save(course);

            var exception = performGet()
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo(COURSE_DOES_NOT_EXIST_MSG.formatted(course.getId()));
        }

        private ResultActions performGet() throws Exception {
            return mockMvc.perform(get(COURSE_URL_PREFIX + "/{id}", course.getId()));
        }
    }
}
