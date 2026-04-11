package casp.web.backend.calendar.data;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.data.participants.CoTrainer;
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
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataMongoTest
class CourseCustomRepositoryImplTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");
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

        calendarEntry = createCalendarEntry(2024);
        course = courseRepository.save(createCourse(calendarEntry));
    }

    @Test
    void findAllByYear() {
        var coursePage = courseRepository.findAllByYear(2024, Pageable.unpaged());

        assertThat(coursePage).containsExactly(course);
    }

    @Test
    void findAllByMemberIdAndNotDeleted() {
        var courseSet = courseRepository.findAllByMemberIdAndNotDeleted(course.getMember().getId());

        assertThat(courseSet).containsExactly(course);
    }

    @Test
    void findAllByMemberIdAndStatus() {
        var courseSet = courseRepository.findAllByMemberIdAndStatus(course.getMember().getId(), EntityStatus.ACTIVE);

        assertThat(courseSet).containsExactly(course);
    }

    @Test
    void getCoursesFeesStats() {
        courseRepository.deleteAll();
        var year = LocalDateTime.now().getYear();
        var thisYearCourse = createCourse(createCalendarEntry(year));
        var inactiveCourse = createCourse(createCalendarEntry(year));
        inactiveCourse.setEntityStatus(EntityStatus.INACTIVE);
        var twoYearsAgoCourse = createCourse(createCalendarEntry(year - 2));
        var threeYearsAgoCourse = createCourse(createCalendarEntry(year - 3));
        var inactiveSpace = createDogHasHandlerReference();
        inactiveSpace.setEntityStatus(EntityStatus.INACTIVE);
        dogHasHandlerReferenceRepository.save(inactiveSpace);
        createSpace(createDogHasHandlerReference(), thisYearCourse);
        createSpace(createDogHasHandlerReference(), thisYearCourse);
        createSpace(inactiveSpace, thisYearCourse);
        createSpace(createDogHasHandlerReference(), inactiveCourse);
        createSpace(createDogHasHandlerReference(), twoYearsAgoCourse);
        createSpace(createDogHasHandlerReference(), threeYearsAgoCourse);

        var coursesFeesStats = courseRepository.getCoursesFeesStats();

        var thisYear = coursesFeesStats.thisYear();
        var lastYear = coursesFeesStats.lastYear();
        var twoYearsAgo = coursesFeesStats.twoYearsAgo();
        assertEquals(year, thisYear.year());
        assertEquals(2.0, thisYear.totalPaid());
        assertEquals(year - 1, lastYear.year());
        assertEquals(0.0, lastYear.totalPaid());
        assertEquals(year - 2, twoYearsAgo.year());
        assertEquals(1.0, twoYearsAgo.totalPaid());
    }

    @Nested
    class FindAllBySpaceId {

        private Space space;
        private DogHasHandlerReference dogHasHandlerReference;

        @BeforeEach
        void setUp() {
            dogHasHandlerReference = createDogHasHandlerReference();
            space = createSpace(dogHasHandlerReference, CourseCustomRepositoryImplTest.this.course);
        }

        @Test
        void multipleCourses() {
            var course2 = createCourse(CourseCustomRepositoryImplTest.this.calendarEntry);
            course2.setSpaceLimit(1);
            course2.addSpace(space);
            courseRepository.save(course2);

            var courseSet = courseRepository.findAllBySpaceId(space.getId(), Pageable.unpaged());

            assertThat(courseSet).containsExactlyInAnyOrder(course, course2);
        }

        @Test
        void oneCourseIsNotActive() {
            var course2 = createCourse(CourseCustomRepositoryImplTest.this.calendarEntry);
            course2.setSpaceLimit(1);
            course2.addSpace(space);
            course2.setEntityStatus(EntityStatus.DELETED);
            courseRepository.save(course2);

            var coursePage = courseRepository.findAllBySpaceId(space.getId(), Pageable.unpaged());

            assertThat(coursePage).containsExactlyInAnyOrder(course);
        }

        @Test
        void spaceIsNotActive() {
            dogHasHandlerReference.setEntityStatus(EntityStatus.DELETED);
            dogHasHandlerReferenceRepository.save(dogHasHandlerReference);
            var course2 = createCourse(CourseCustomRepositoryImplTest.this.calendarEntry);
            course2.setSpaceLimit(1);
            course2.addSpace(space);
            courseRepository.save(course2);

            var coursePage = courseRepository.findAllBySpaceId(space.getId(), Pageable.unpaged());

            assertThat(coursePage).isEmpty();
        }
    }

    @Nested
    class FindAllBetweenFromAndTo {
        @Test
        void beforeTheCourse() {
            var actualCourses = courseRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom().minusDays(1), calendarEntry.getEntryFrom().minusHours(1), null);

            assertThat(actualCourses).isEmpty();
        }

        @Test
        void afterTheCourse() {
            var actualCourses = courseRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryTo().plusHours(1), calendarEntry.getEntryTo().plusDays(1), null);

            assertThat(actualCourses).isEmpty();
        }

        @Test
        void fromIsLessThanMin() {
            var actualCourses = courseRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom().minusDays(1), calendarEntry.getEntryTo(), null);

            assertThat(actualCourses).containsExactly(course);
        }

        @Test
        void toIsMoreThanMax() {
            var actualCourses = courseRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo().plusDays(1), null);

            assertThat(actualCourses).containsExactly(course);
        }

        @Test
        void fromIsLittleMoreThanMinAndToIsLittleLessThanMax() {
            var actualCourses = courseRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom().plusHours(1), calendarEntry.getEntryTo().minusHours(1), null);

            assertThat(actualCourses).containsExactly(course);
        }
    }

    @Nested
    class FindAllByMemberId {
        private Course course2;

        @BeforeEach
        void setUp() {
            course2 = courseRepository.save(createCourse(CourseCustomRepositoryImplTest.this.calendarEntry));
        }

        @Test
        void memberIdIsNull() {
            var actualCourses = courseRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo(), null);

            assertThat(actualCourses).containsExactlyInAnyOrder(course, course2);
        }

        @Test
        void memberIdIsTheSameAsCourseMember() {
            var actualCourses = courseRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo(), course2.member.getId());

            assertThat(actualCourses).containsExactly(course2);
        }

        @Test
        void memberIdIsTheSameAsCourseCoTrainer() {
            var coTrainer = new CoTrainer(createMemberReference());
            course2.setCoTrainers(Set.of(coTrainer));
            courseRepository.save(course2);

            var actualCourses = courseRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo(), coTrainer.getId());

            assertThat(actualCourses).containsExactly(course2);
        }

        @Test
        void memberIdIsTheSameAsCourseSpace() {
            var space = new Space(createDogHasHandlerReference());
            course2.addSpace(space);
            courseRepository.save(course2);

            var actualCourses = courseRepository.findAllBetweenFromAndToOrMemberId(calendarEntry.getEntryFrom(), calendarEntry.getEntryTo(), space.getDogHasHandler().getMember().getId());

            assertThat(actualCourses).containsExactly(course2);
        }
    }

    private static CalendarEntry createCalendarEntry(int year) {
        var thisYearEntry = new CalendarEntry();
        thisYearEntry.setEntryFrom(LocalDateTime.of(year, 2, 1, 3, 0));
        thisYearEntry.setEntryTo(thisYearEntry.getEntryFrom().plusHours(10));
        return thisYearEntry;
    }

    private DogHasHandlerReference createDogHasHandlerReference() {
        var dogReference = new DogReference();
        dogReference.setName("Max");
        var dogHasHandlerReference = new DogHasHandlerReference();
        dogHasHandlerReference.setDog(dogReferenceRepository.save(dogReference));
        dogHasHandlerReference.setMember(createMemberReference());
        return dogHasHandlerReferenceRepository.save(dogHasHandlerReference);
    }

    private Course createCourse(CalendarEntry localCalendarEntry) {
        var newCourse = new Course();
        newCourse.setMember(createMemberReference());
        newCourse.addCalendarEntry(localCalendarEntry);
        newCourse.setMinTime(localCalendarEntry.getEntryFrom());
        newCourse.setMaxTime(localCalendarEntry.getEntryTo());
        return newCourse;
    }

    private MemberReference createMemberReference() {
        var member = ReferenceTestFixture.createMemberReference();
        return memberReferenceRepository.save(member);
    }

    private Space createSpace(DogHasHandlerReference dogHasHandlerReference, Course localCourse) {
        var space = new Space(dogHasHandlerReference);
        space.setPaidDate(localCourse.getMinTime().toLocalDate());
        space.setPaidPrice(1.0);
        localCourse.setSpaceLimit(localCourse.getSpaceLimit() + 1);
        localCourse.addSpace(space);
        courseRepository.save(localCourse);
        return space;
    }
}
