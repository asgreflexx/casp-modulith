package casp.web.backend;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.options.WeeklyEventOptionRecurrence;
import casp.web.backend.data.access.layer.documents.event.participant.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.participant.Space;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.data.access.layer.documents.member.Member;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public final class TestFixture {
    private static final Validator VALIDATOR;

    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = validatorFactory.getValidator();
        }
    }

    private TestFixture() {
    }

    public static <T> Set<ConstraintViolation<T>> getViolations(final T object) {
        return VALIDATOR.validate(object);
    }

    public static WeeklyEventOptionRecurrence createValidWeeklyEventOptionRecurrence() {
        var weeklyEventOptionRecurrence = new WeeklyEventOptionRecurrence();
        weeklyEventOptionRecurrence.setDayOfWeek(DayOfWeek.MONDAY);
        weeklyEventOptionRecurrence.setStartTime(LocalTime.MIN);
        weeklyEventOptionRecurrence.setEndTime(LocalTime.MAX);
        return weeklyEventOptionRecurrence;
    }

    public static Event createValidEvent() {
        var member = createValidMember();
        var event = new Event();
        event.setName("Test Event");
        event.setMemberId(member.getId());
        event.setMember(member);
        return event;
    }

    public static Member createValidMember() {
        var member = new Member();
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("john.doe@example.com");
        return member;
    }

    public static Calendar createValidCalendarEntry(final BaseEvent baseEvent) {
        var calendar = new Calendar();
        calendar.setEventFrom(LocalDateTime.now());
        calendar.setEventTo(LocalDateTime.now().plusHours(1));
        calendar.setBaseEvent(baseEvent);
        return calendar;
    }

    public static Calendar createValidCalendarEntry() {
        return createValidCalendarEntry(createValidEvent());
    }

    public static Exam createValidExam() {
        var member = createValidMember();
        var exam = new Exam();
        exam.setName("Exam 1");
        exam.setMemberId(member.getId());
        exam.setJudgeName("Judge");
        exam.setMember(member);
        return exam;
    }

    public static Course ceateValidCourse() {
        var member = createValidMember();
        var course = new Course();
        course.setName("Course Name");
        course.setMemberId(member.getId());
        course.setMember(member);
        return course;
    }

    public static Space createValidSpace() {
        var space = new Space();
        space.setMemberOrHandlerId(UUID.randomUUID());
        space.setBaseEvent(createValidEvent());
        return space;
    }

    public static DogHasHandler createValidDogHasHandler() {
        var member = createValidMember();
        var dog = createValidDog();
        var dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(dog.getId());
        dogHasHandler.setDog(dog);
        dogHasHandler.setMemberId(member.getId());
        dogHasHandler.setMember(member);
        return dogHasHandler;
    }

    public static Dog createValidDog() {
        var dog = new Dog();
        dog.setName("Riley");
        dog.setOwnerName("John Doe");
        dog.setOwnerAddress("123 Main St");
        return dog;
    }

    public static EventParticipant createValidEventParticipant() {
        return createValidEventParticipant(createValidEvent());
    }

    public static EventParticipant createValidEventParticipant(final Event event) {
        var eventParticipant = new EventParticipant();
        eventParticipant.setMemberOrHandlerId(createValidMember().getId());
        eventParticipant.setBaseEvent(event);
        return eventParticipant;
    }
}
