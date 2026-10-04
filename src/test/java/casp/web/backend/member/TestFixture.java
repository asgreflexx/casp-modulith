package casp.web.backend.member;

import casp.web.backend.member.data.Member;

public enum TestFixture {
    ;

    public static Member createMember() {
        var member = new Member();
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("%s.%s@example.com".formatted(member.getFirstName(), member.getLastName()));
        return member;
    }
}
