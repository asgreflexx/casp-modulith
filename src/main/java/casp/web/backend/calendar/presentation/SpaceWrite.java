package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.data.participants.EventResponse;
import casp.web.backend.common.reference.DogHasHandlerReference;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class SpaceWrite implements SpaceWriteRequiredFields {
    private DogHasHandlerReference dogHasHandler;
    private String note;
    private Double paidPrice;
    private LocalDate paidDate;
    private EventResponse response;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpaceWrite that)) return false;
        return Objects.equals(getDogHasHandler().getId(), that.getDogHasHandler().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDogHasHandler().getId());
    }
}
