package casp.web.backend.calendar.data;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.data.participants.Space;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.DogReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CourseCustomRepositoryImplTest {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;
    @Autowired
    private DogReferenceRepository dogReferenceRepository;

    private Course course;
    private CalendarEntry calendarEntry;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        dogHasHandlerReferenceRepository.deleteAll();
        dogReferenceRepository.deleteAll();
        memberReferenceRepository.deleteAll();

        calendarEntry = new CalendarEntry();
        calendarEntry.setEntryFrom(LocalDateTime.of(2024, 1, 1, 0, 0));
        calendarEntry.setEntryTo(calendarEntry.getEntryFrom().plusHours(10));

        course = courseRepository.save(createCourse());
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

    private Course createCourse() {
        var course = new Course();
        course.setMember(createMemberReference());
        course.addCalendarEntry(calendarEntry);
        return course;
    }

    private MemberReference createMemberReference() {
        var member = ReferenceTestFixture.createMemberReference();
        return memberReferenceRepository.save(member);
    }

    @Nested
    class FindAllBySpace {

        private Space space;

        @BeforeEach
        void setUp() {
            space = new Space(createDogHasHandlerReference());
            course.setSpaceLimit(course.getSpaceLimit() + 1);
            course.addSpace(space);
            courseRepository.save(course);
        }

        @Test
        void multipleCourses() {
            var course2 = createCourse();
            course2.setSpaceLimit(1);
            course2.addSpace(space);
            courseRepository.save(course2);

            var courseSet = courseRepository.findAllBySpace(space, Pageable.unpaged());

            assertThat(courseSet).
                    containsExactlyInAnyOrder(course, course2);
        }

        @Test
        void oneCourseIsNotActive() {
            var course2 = createCourse();
            course2.setSpaceLimit(1);
            course2.addSpace(space);
            course2.setEntityStatus(EntityStatus.DELETED);
            courseRepository.save(course2);

            var coursePage = courseRepository.findAllBySpace(space, Pageable.unpaged());

            assertThat(coursePage).
                    containsExactlyInAnyOrder(course);
        }

        private DogHasHandlerReference createDogHasHandlerReference() {
            var dogReference = new DogReference();
            dogReference.setName("Max");
            var dogHasHandlerReference = new DogHasHandlerReference();
            dogHasHandlerReference.setDog(dogReferenceRepository.save(dogReference));
            dogHasHandlerReference.setMember(createMemberReference());
            return dogHasHandlerReferenceRepository.save(dogHasHandlerReference);
        }
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
