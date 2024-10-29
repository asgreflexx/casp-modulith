package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CourseCustomRepositoryImplTest {
    @Autowired
    private CourseRepository courseRepository;
    private Course course;
    private CalendarEntry calendarEntry;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();

        calendarEntry = new CalendarEntry();
        calendarEntry.setEntryFrom(LocalDateTime.of(2024, 1, 1, 0, 0));
        calendarEntry.setEntryTo(calendarEntry.getEntryFrom().plusHours(10));
        course = new Course();
        course.setMember(new MemberReference());
        course.setCalendarEntries(new ArrayList<>(List.of(calendarEntry)));

        course = courseRepository.save(course);
    }

    @Test
    void findAllByYear() {
        var coursePage = courseRepository.findAllByYear(2024, Pageable.unpaged());

        assertThat(coursePage)
                .containsExactly(course);
    }

    @Test
    void findAllByMemberIdAndNotDeleted() {
        var courseSet = courseRepository.findAllByMemberIdAndNotDeleted(course.getMember().getId());

        assertThat(courseSet)
                .containsExactly(course);
    }

    @Test
    void findAllByMemberIdAndStatus() {
        var courseSet = courseRepository.findAllByMemberIdAndStatus(course.getMember().getId(), EntityStatus.ACTIVE);

        assertThat(courseSet)
                .containsExactly(course);
    }

    @Nested
    class FindAllBetweenFromAndTo {
        @Test
        void beforeTheCourse() {
            var actualCourses = courseRepository.findAllBetweenFromAndTo(calendarEntry.getEntryFrom().minusDays(1), calendarEntry.getEntryFrom().minusHours(1));

            assertThat(actualCourses).isEmpty();
        }

        @Test
        void afterTheCourse() {
            var actualCourses = courseRepository.findAllBetweenFromAndTo(calendarEntry.getEntryTo().plusHours(1), calendarEntry.getEntryTo().plusDays(1));

            assertThat(actualCourses).isEmpty();
        }

        @Test
        void fromIsLessThanMin() {
            var actualCourses = courseRepository.findAllBetweenFromAndTo(calendarEntry.getEntryFrom().minusDays(1), calendarEntry.getEntryTo());

            assertThat(actualCourses).containsExactly(course);
        }

        @Test
        void toIsMoreThanMax() {
            var actualCourses = courseRepository.findAllBetweenFromAndTo(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo().plusDays(1));

            assertThat(actualCourses).containsExactly(course);
        }

        @Test
        void fromIsLittleMoreThanMinAndToIsLittleLessThanMax() {
            var actualCourses = courseRepository.findAllBetweenFromAndTo(calendarEntry.getEntryFrom().plusHours(1), calendarEntry.getEntryTo().minusHours(1));

            assertThat(actualCourses).containsExactly(course);
        }
    }
}
