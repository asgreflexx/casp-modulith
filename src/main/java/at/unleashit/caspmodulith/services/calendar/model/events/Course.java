package at.unleashit.caspmodulith.services.calendar.model.events;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@QueryEntity
@Document(BaseEvent.COLLECTION)
@TypeAlias(Course.EVENT_TYPE)
public class Course extends BaseEvent {
    public static final String EVENT_TYPE = "COURSE";

    @PositiveOrZero
    private int spaceLimit;

    public Course() {
        super(EVENT_TYPE);
    }

    public int getSpaceLimit() {
        return spaceLimit;
    }

    public void setSpaceLimit(int spaceLimit) {
        this.spaceLimit = spaceLimit;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
