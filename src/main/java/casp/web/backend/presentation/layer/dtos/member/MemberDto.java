package casp.web.backend.presentation.layer.dtos.member;

import casp.web.backend.data.access.layer.documents.member.Member;

import java.util.HashSet;
import java.util.Set;

public class MemberDto extends Member {
    private Set<CardDto> cardDtoSet = new HashSet<>();

    public Set<CardDto> getCardDtoSet() {
        return cardDtoSet;
    }

    public void setCardDtoSet(final Set<CardDto> cardDtoSet) {
        cardDtoSet.forEach(c -> {
            c.setMember(null);
            c.setMemberId(null);
        });
        this.cardDtoSet = cardDtoSet;
    }
}
