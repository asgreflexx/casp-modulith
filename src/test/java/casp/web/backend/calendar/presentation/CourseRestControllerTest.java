package casp.web.backend.calendar.presentation;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.CourseDto;
import casp.web.backend.calendar.CourseService;
import casp.web.backend.calendar.data.participants.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.calendar.presentation.CourseReadMapper.COURSE_READ_MAPPER;
import static casp.web.backend.calendar.presentation.CourseWriteMapper.COURSE_WRITE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseRestControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseRestController courseRestController;

    private CourseDto courseDto;

    @BeforeEach
    void setUp() {
        courseDto = new CourseDto();
        courseDto.setName("Test Course");
    }

    @Test
    void save() {
        var courseWrite = mock(CourseWrite.class);
        when(courseWrite.getName()).thenReturn("course");

        var response = courseRestController.save(courseWrite);

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(courseService).save(argThat(actualCourseDto -> courseWrite.getName().equals(actualCourseDto.getName())));
    }

    @Test
    void getOneById() {
        when(courseService.getOneById(courseDto.getId())).thenReturn(courseDto);

        var response = courseRestController.getOneById(courseDto.getId());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(COURSE_READ_MAPPER.toTarget(courseDto));
    }

    @Test
    void deleteById() {
        var response = courseRestController.deleteById(courseDto.getId());

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(courseService).deleteById(courseDto.getId());
    }

    @Test
    void getAllByYear() {
        when(courseService.getAllByYear(2024, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(courseDto)));

        var response = courseRestController.getAllByYear(2024, Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(COURSE_READ_MAPPER.toTarget(courseDto));
    }

    @Test
    void getSpacesEmail() {
        when(courseService.getEmailsByCourseId(courseDto.getId())).thenReturn(Set.of("mail"));

        var response = courseRestController.getSpacesEmail(courseDto.getId());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly("mail");
    }

    @Test
    void updateSpace() {
        var dogHasHandler = ReferenceTestFixture.createDogHasHandlerReference();
        var spaceWrite = new SpaceWrite();
        spaceWrite.setDogHasHandler(dogHasHandler);
        var spaceWriteSet = Set.of(spaceWrite);
        var space = new Space(dogHasHandler);
        courseDto.setParticipants(Set.of(space));
        when(courseService.updateSpaces(courseDto.getId(), courseDto.getVersion(), COURSE_WRITE_MAPPER.toSpaceDtos(spaceWriteSet))).thenReturn(courseDto);

        var response = courseRestController.updateSpaces(courseDto.getId(), courseDto.getVersion(), spaceWriteSet);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(COURSE_READ_MAPPER.toTarget(courseDto));
    }

    @Test
    void getCoursesByDogHasHandlerId() {
        var dogHasHandlerId = UUID.randomUUID();
        when(courseService.getCoursesByDogHasHandlerId(dogHasHandlerId, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(courseDto)));

        var response = courseRestController.getCoursesByDogHasHandlerId(dogHasHandlerId, Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(COURSE_READ_MAPPER.toTarget(courseDto));
    }

    @Test
    void migrateDataToV2() {
        var response = courseRestController.migrateDataToV2();

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(courseService).migrateDataToV2();
    }
}
