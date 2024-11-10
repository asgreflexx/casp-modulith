package casp.web.backend;

import casp.web.backend.dog.data.Dog;
import casp.web.backend.member.data.Member;

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


    public static Dog createDog() {
        var dog = new Dog();
        dog.setName("Riley");
        dog.setOwnerName("John Doe");
        dog.setOwnerAddress("123 Main St");
        return dog;
    }

}
