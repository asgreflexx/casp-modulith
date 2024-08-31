package casp.web.backend.business.logic.layer.events.dtos;


import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.events.mappers.CourseMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CourseDtoTest {

    private CourseDto courseDto;

    @BeforeEach
    void setUp() {
        var course = TestFixture.ceateValidCourse();
        courseDto = new CourseMapperImpl().documentToDto(course);
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
        courseDto.setParticipants(Set.of(john, maria));

        assertThat(TestFixture.getViolations(courseDto)).hasSize(1);
    }

    @Test
    void trainersIsNull() {
        courseDto.setTrainers(null);

        assertThat(TestFixture.getViolations(courseDto)).hasSize(1);
    }
}
