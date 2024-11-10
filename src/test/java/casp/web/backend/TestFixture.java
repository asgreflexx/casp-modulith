package casp.web.backend;

import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.dog.data.Dog;

public enum TestFixture {
    ;

    public static Dog createDog() {
        var dog = new Dog();
        dog.setName("Riley");
        dog.setOwnerName("John Doe");
        dog.setOwnerAddress("123 Main St");
        return dog;
    }

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
}
