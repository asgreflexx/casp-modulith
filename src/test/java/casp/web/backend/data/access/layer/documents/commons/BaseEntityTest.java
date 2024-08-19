package casp.web.backend.data.access.layer.documents.commons;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.dog.Grade;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.GradeType;
import casp.web.backend.data.access.layer.documents.event.options.WeeklyEventOptionRecurrence;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.documents.member.Member;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class BaseEntityTest {
    private final Validator validator;

    protected BaseEntityTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    protected static Dog createValidDog() {
        var dog = new Dog();
        dog.setName("Riley");
        dog.setOwnerName("John Doe");
        dog.setOwnerAddress("123 Main St");
        return dog;
    }

    protected static WeeklyEventOptionRecurrence createValidWeeklyEventOptionRecurrence() {
        var weeklyEventOptionRecurrence = new WeeklyEventOptionRecurrence();
        weeklyEventOptionRecurrence.setDayOfWeek(DayOfWeek.MONDAY);
        weeklyEventOptionRecurrence.setStartTime(LocalTime.MIN);
        weeklyEventOptionRecurrence.setEndTime(LocalTime.MAX);
        return weeklyEventOptionRecurrence;
    }

    protected static Event createValidEvent() {
        var event = new Event();
        event.setName("Test Event");
        event.setMemberId(UUID.randomUUID());
        event.setMember(createValidMember());
        return event;
    }

    protected static Member createValidMember() {
        var member = new Member();
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("john.doe@example.com");
        return member;
    }

    protected static Grade createValidGrade() {
        var grade = new Grade();
        grade.setName("grade1");
        grade.setType(GradeType.BH1);
        grade.setPoints(1);
        grade.setExamDate(LocalDate.now());
        return grade;
    }

    protected <T> Set<ConstraintViolation<T>> getViolations(T object) {
        return validator.validate(object);
    }

    protected <T extends BaseEntity> void baseAssertions(T baseEntity) {
        assertSame(EntityStatus.ACTIVE, baseEntity.getEntityStatus());
        assertNotNull(baseEntity.getId());
    }
}
