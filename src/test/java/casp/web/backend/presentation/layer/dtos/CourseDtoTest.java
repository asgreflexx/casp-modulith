package casp.web.backend.presentation.layer.dtos;


import casp.web.backend.TestFixture;
import casp.web.backend.presentation.layer.dtos.event.types.CourseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static casp.web.backend.presentation.layer.dtos.event.participants.SpaceMapper.SPACE_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.CourseMapper.COURSE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;

class CourseDtoTest {

    private CourseDto courseDto;

    @BeforeEach
    void setUp() {
        var course = TestFixture.createValidCourse();
        courseDto = COURSE_MAPPER.toDto(course);
        courseDto.setCalendarEntries(List.of(TestFixture.createValidCalendarEntry(course)));
    }

    @Test
    void happyPath() {
        assertThat(TestFixture.getViolations(courseDto)).isEmpty();
    }

    @Test
    void participantsIsNull() {
        courseDto.setParticipants(null);

        assertThat(TestFixture.getViolations(courseDto)).hasSize(1);
    }

    @Test
    void tooManyParticipants() {
        courseDto.setSpaceLimit(1);
        var john = TestFixture.createValidSpace();
        var maria = TestFixture.createValidSpace();
        courseDto.setParticipants(SPACE_MAPPER.toDtoSet(Set.of(john, maria)));

        assertThat(TestFixture.getViolations(courseDto)).hasSize(1);
    }

    @Test
    void trainersIsNull() {
        courseDto.setCoTrainers(null);

        assertThat(TestFixture.getViolations(courseDto)).hasSize(1);
    }
}
