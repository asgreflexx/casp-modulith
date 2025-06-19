package casp.web.backend;

import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;

public enum ReferenceTestFixture {
    ;

    public static MemberReference createMemberReference() {
        return createMemberReference("John", "Doe");
    }

    public static MemberReference createMemberReference(String firstName, String lastName) {
        var member = new MemberReference();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail("%s@mail.com".formatted(member.getId()));
        return member;
    }

    public static DogReference createDogReference() {
        return createDogReference("Riley");
    }

    public static DogReference createDogReference(String name) {
        var dogReference = new DogReference();
        dogReference.setName(name);
        return dogReference;
    }

    public static DogHasHandlerReference createDogHasHandlerReference(String dogName, String firstName, String lastName) {
        var dogReference = createDogReference(dogName);
        var memberReference = createMemberReference(firstName, lastName);
        var dogHasHandlerReference = new DogHasHandlerReference();
        dogHasHandlerReference.setDog(dogReference);
        dogHasHandlerReference.setMember(memberReference);
        return dogHasHandlerReference;
    }
}
