package casp.web.backend.member;

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
}
