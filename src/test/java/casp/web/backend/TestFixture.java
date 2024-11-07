package casp.web.backend;

import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.deprecated.dog.DogHasHandler;
import casp.web.backend.deprecated.event.participants.ExamParticipant;
import casp.web.backend.deprecated.event.participants.Space;
import casp.web.backend.deprecated.event.types.Course;
import casp.web.backend.deprecated.event.types.Exam;

public enum TestFixture {
    ;

    public static Member createMember() {
        return createMember("John", "Doe");
    }

    public static Member createMember(String firstName, String lastName) {
        var member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail("%s@example.com".formatted(member.getId()));
        return member;
    }

    public static Exam createExam() {
        var member = createMember();
        var exam = new Exam();
        exam.setName("Exam 1");
        exam.setMemberId(member.getId());
        exam.setJudgeName("Judge");
        exam.setMember(member);
        return exam;
    }

    public static Course createCourse() {
        var member = createMember();
        var course = new Course();
        course.setName("Course Name");
        course.setMemberId(member.getId());
        course.setMember(member);
        return course;
    }

    public static Space createSpace() {
        return createSpace(createCourse());
    }

    public static Space createSpace(Course course) {
        var space = new Space();
        var dogHasHandler = createDogHasHandler();
        space.setMemberOrHandlerId(dogHasHandler.getId());
        space.setDogHasHandler(dogHasHandler);
        space.setBaseEvent(course);
        return space;
    }

    public static DogHasHandler createDogHasHandler() {
        var member = createMember();
        var dog = createDog();
        return createDogHasHandler(dog, member);
    }

    public static DogHasHandler createDogHasHandler(Dog dog, Member member) {
        var dogHasHandler = new DogHasHandler();
        dogHasHandler.setDogId(dog.getId());
        dogHasHandler.setDog(dog);
        dogHasHandler.setMemberId(member.getId());
        dogHasHandler.setMember(member);
        return dogHasHandler;
    }

    public static Dog createDog() {
        var dog = new Dog();
        dog.setName("Riley");
        dog.setOwnerName("John Doe");
        dog.setOwnerAddress("123 Main St");
        return dog;
    }

    public static ExamParticipant createExamParticipant() {
        return createExamParticipant(createExam());
    }

    public static ExamParticipant createExamParticipant(Exam exam) {
        var examParticipant = new ExamParticipant();
        examParticipant.setMemberOrHandlerId(createDogHasHandler().getId());
        examParticipant.setBaseEvent(exam);
        return examParticipant;
    }

}
