package casp.web.backend.dog.presentation;

import casp.web.backend.common.base.BaseView;
import casp.web.backend.dog.DogHasHandlerDtoRequiredFields;
import casp.web.backend.dog.data.Grade;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class DogHasHandlerWrite extends BaseView implements DogHasHandlerDtoRequiredFields {
    private Set<Grade> grades = new HashSet<>();
    private UUID memberId;
    private UUID dogId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DogHasHandlerWrite that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(memberId, that.memberId) && Objects.equals(dogId, that.dogId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, dogId);
    }
}
