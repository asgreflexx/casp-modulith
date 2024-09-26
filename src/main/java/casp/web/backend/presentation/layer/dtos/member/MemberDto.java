package casp.web.backend.presentation.layer.dtos.member;

import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerDto;

import java.util.HashSet;
import java.util.Set;

public class MemberDto extends Member {
    private Set<CardDto> cardDtoSet = new HashSet<>();
    private Set<DogHasHandlerDto> dogHasHandlerDtoSet = new HashSet<>();

    public Set<CardDto> getCardDtoSet() {
        return cardDtoSet;
    }

    public void setCardDtoSet(final Set<CardDto> cardDtoSet) {
        this.cardDtoSet = cardDtoSet;
    }

    public Set<DogHasHandlerDto> getDogHasHandlerDtoSet() {
        return dogHasHandlerDtoSet;
    }

    public void setDogHasHandlerDtoSet(final Set<DogHasHandlerDto> dogHasHandlerDtoSet) {
        this.dogHasHandlerDtoSet = dogHasHandlerDtoSet;
    }
}
