package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.CoTrainerService;
import casp.web.backend.business.logic.layer.event.participants.SpaceService;
import casp.web.backend.business.logic.layer.event.types.CourseService;
import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.CourseMapper.COURSE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CourseFacadeImplTest {
    @Mock
    private CoTrainerService coTrainerService;
    @Mock
    private SpaceService spaceService;
    @Mock
    private CalendarService calendarService;
    @Mock
    private CourseService courseService;

    @Spy
    @InjectMocks
    private CourseFacadeImpl courseFacade;

    @Test
    void getById() {
        var course = TestFixture.createCourse();
        when(courseService.getById(course.getId())).thenReturn(course);

        var courseDto = courseFacade.getById(course.getId());

        assertEquals(course.getId(), courseDto.getId());
        verify(courseFacade).mapDocumentToDto(course);
    }

    @Test
    void deleteById() {
        var id = UUID.randomUUID();
        courseFacade.deleteById(id);

        verify(courseService).deleteBaseEventById(id);
    }

    @Nested
    class Save {
        @Captor
        private ArgumentCaptor<Course> courseCaptor;
        @Captor
        private ArgumentCaptor<List<Calendar>> calendarListCaptor;
        @Captor
        private ArgumentCaptor<Set<UUID>> idSetCaptor;

        private CourseDto courseDto;
        private Course course;

        @BeforeEach
        void setUp() {
            course = TestFixture.createCourse();
            courseDto = COURSE_MAPPER.toDto(course);
        }

        @Test
        void replaceCalendarEntries() {
            var calendarEntry = TestFixture.createCalendarEntry();
            var calendarDtoList = CALENDAR_MAPPER.toDtoList(List.of(calendarEntry));
            courseDto.setCalendarEntries(calendarDtoList);

            courseFacade.save(courseDto);

            verify(calendarService).replaceCalendarEntries(courseCaptor.capture(), calendarListCaptor.capture());
            assertEquals(course.getId(), courseCaptor.getValue().getId());
            assertThat(calendarListCaptor.getValue()).singleElement().satisfies(actual -> {
                assertEquals(calendarEntry.getEventFrom(), actual.getEventFrom());
                assertEquals(calendarEntry.getEventTo(), actual.getEventTo());
            });
        }

        @Test
        void replaceSpaces() {
            var space = TestFixture.createSpace();
            courseDto.setParticipantsIdToWrite(Set.of(space.getId()));

            courseFacade.save(courseDto);

            verify(spaceService).replaceParticipants(courseCaptor.capture(), idSetCaptor.capture());
            assertEquals(course.getId(), courseCaptor.getValue().getId());
            assertThat(idSetCaptor.getValue()).singleElement().isEqualTo(space.getId());
        }

        @Test
        void replaceCoTrainers() {
            var coTrainer = TestFixture.createCoTrainer();
            courseDto.setCoTrainersIdToWrite(Set.of(coTrainer.getId()));

            courseFacade.save(courseDto);

            verify(coTrainerService).replaceParticipants(courseCaptor.capture(), idSetCaptor.capture());
            assertEquals(course.getId(), courseCaptor.getValue().getId());
            assertThat(idSetCaptor.getValue()).singleElement().isEqualTo(coTrainer.getId());
        }

        @Test
        void saveCourse() {
            courseFacade.save(courseDto);

            verify(courseService).save(courseCaptor.capture());
            assertEquals(course.getId(), courseCaptor.getValue().getId());
        }
    }

    @Nested
    class MapDocumentToDto {
        @Test
        void mapCoTrainer() {
            var coTrainer = TestFixture.createCoTrainer();
            var course = coTrainer.getBaseEvent();
            var member = course.getMember();
            coTrainer.setMemberOrHandlerId(member.getId());
            coTrainer.setMember(member);
            when(coTrainerService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(course.getId())).thenReturn(Set.of(coTrainer));

            var courseDto = courseFacade.mapDocumentToDto(course);

            assertThat(courseDto.getCoTrainersToRead())
                    .singleElement()
                    .satisfies(actual -> {
                        assertEquals(coTrainer.getId(), actual.getId());
                        assertEquals(coTrainer.getMemberOrHandlerId(), actual.getMember().getId());
                    });
        }

        @Test
        void mapSpace() {
            var dogHasHandler = TestFixture.createDogHasHandler();
            var space = TestFixture.createSpace();
            space.setMemberOrHandlerId(dogHasHandler.getId());
            var course = space.getBaseEvent();
            space.setDogHasHandler(dogHasHandler);
            when(spaceService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(course.getId())).thenReturn(Set.of(space));

            var courseDto = courseFacade.mapDocumentToDto(course);

            assertThat(courseDto.getParticipantsToRead())
                    .singleElement()
                    .satisfies(actual -> {
                        assertEquals(space.getId(), actual.getId());
                        assertEquals(space.getMemberOrHandlerId(), actual.getDogHasHandler().getId());
                    });
        }

        @Test
        void mapDailyEventOption() {
            var dailyEventOption = TestFixture.createDailyEventOption();
            var course = TestFixture.createCourse();
            course.setDailyOption(dailyEventOption);

            var courseDto = courseFacade.mapDocumentToDto(course);

            assertEquals(dailyEventOption.getOptionType(), courseDto.getOption().getOptionType());
        }

        @Test
        void mapWeeklyEventOption() {
            var weeklyEventOption = TestFixture.createWeeklyEventOption();
            var course = TestFixture.createCourse();
            course.setWeeklyOption(weeklyEventOption);

            var courseDto = courseFacade.mapDocumentToDto(course);

            assertEquals(weeklyEventOption.getOptionType(), courseDto.getOption().getOptionType());
        }

        @Test
        void mapWrongBaseEventType() {
            var event = TestFixture.createEvent();

            assertThrows(IllegalArgumentException.class, () -> courseFacade.mapDocumentToDto(event));
        }
    }
}
