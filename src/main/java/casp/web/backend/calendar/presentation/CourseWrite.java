package casp.web.backend.calendar.presentation;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Setter
@Getter
public class CourseWrite extends BaseEventWrite {
    @JsonSetter(nulls = Nulls.SKIP)
    private Set<UUID> coTrainerIds = new HashSet<>();
    private int spaceLimit;
}
