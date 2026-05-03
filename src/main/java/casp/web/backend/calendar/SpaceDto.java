package casp.web.backend.calendar;

import casp.web.backend.calendar.data.participants.EventResponse;
import casp.web.backend.calendar.presentation.SpaceWriteRequiredFields;
import casp.web.backend.common.reference.DogHasHandlerReference;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class SpaceDto implements SpaceWriteRequiredFields {
    private UUID courseId;
    private String courseName;
    private DogHasHandlerReference dogHasHandler;
    private String note;
    private Double paidPrice;
    private LocalDate paidDate;
    private EventResponse response;

    public UUID getId() {
        return dogHasHandler.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpaceDto spaceDto)) return false;
        return Objects.equals(courseId, spaceDto.courseId) && Objects.equals(getId(), spaceDto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, getId());
    }
}
