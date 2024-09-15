package casp.web.backend;

import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.options.DailyEventOption;
import casp.web.backend.data.access.layer.event.options.WeeklyEventOption;
import casp.web.backend.data.access.layer.event.options.WeeklyEventOptionRecurrence;
import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.data.access.layer.member.Card;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MembershipFee;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

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
        return createValidMember("John", "Doe");
    }

    public static Member createValidMember(final String firstName, final String lastName) {
        var member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail("%s@example.com".formatted(member.getId()));
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

    public static Course createValidCourse() {
        var member = createValidMember();
        var course = new Course();
        course.setName("Course Name");
        course.setMemberId(member.getId());
        course.setMember(member);
        return course;
    }

    public static Space createValidSpace() {
        return createValidSpace(createValidCourse());
    }

    public static Space createValidSpace(final Course course) {
        var space = new Space();
        space.setMemberOrHandlerId(createValidDogHasHandler().getId());
        space.setBaseEvent(course);
        return space;
    }

    public static DogHasHandler createValidDogHasHandler() {
        var member = createValidMember();
        var dog = createValidDog();
        return createValidDogHasHandler(dog, member);
    }

    public static DogHasHandler createValidDogHasHandler(final Dog dog, final Member member) {
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

    public static ExamParticipant createValidExamParticipant() {
        return createValidExamParticipant(createValidExam());
    }

    public static ExamParticipant createValidExamParticipant(final Exam exam) {
        var examParticipant = new ExamParticipant();
        examParticipant.setMemberOrHandlerId(createValidDogHasHandler().getId());
        examParticipant.setBaseEvent(exam);
        return examParticipant;
    }

    public static CoTrainer createValidCoTrainer() {
        return createValidCoTrainer(createValidCourse());
    }

    public static CoTrainer createValidCoTrainer(final Course course) {
        var coTrainer = new CoTrainer();
        coTrainer.setBaseEvent(course);
        coTrainer.setMemberOrHandlerId(createValidMember().getId());
        return coTrainer;
    }

    public static DailyEventOption createValidDailyEventOption() {
        var dailyEventOption = new DailyEventOption();
        dailyEventOption.setStartRecurrence(LocalDate.MIN);
        dailyEventOption.setEndRecurrence(LocalDate.MAX);
        dailyEventOption.setStartTime(LocalTime.MIN);
        dailyEventOption.setEndTime(LocalTime.MAX);
        return dailyEventOption;
    }

    public static WeeklyEventOption createValidWeeklyEventOption() {
        var weeklyEventOption = new WeeklyEventOption();
        weeklyEventOption.setStartRecurrence(LocalDate.MIN);
        weeklyEventOption.setEndRecurrence(LocalDate.MAX);
        weeklyEventOption.setOccurrences(List.of(createValidWeeklyEventOptionRecurrence()));
        return weeklyEventOption;
    }

    public static Card createValidCard() {
        var member = createValidMember();
        return createValidCard(member);
    }

    public static Card createValidCard(final Member member) {
        var card = new Card();
        card.setCode("code");
        card.setMemberId(member.getId());
        card.setMember(member);
        return card;
    }

    public static MembershipFee createValidMembershipFee() {
        var membershipFee = new MembershipFee();
        membershipFee.setPaid(true);
        membershipFee.setPaidDate(LocalDate.now());
        membershipFee.setPaidPrice(10.0);
        return membershipFee;
    }
}
