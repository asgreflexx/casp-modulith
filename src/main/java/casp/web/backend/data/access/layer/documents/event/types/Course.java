package casp.web.backend.data.access.layer.documents.event.types;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.StringJoiner;


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
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Course.class.getSimpleName() + "[", "]")
                .add("spaceLimit=" + spaceLimit)
                .add("eventType='" + eventType + "'")
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("memberId=" + memberId)
                .add("dailyOption=" + dailyOption)
                .add("weeklyOption=" + weeklyOption)
                .add("minLocalDateTime=" + minLocalDateTime)
                .add("maxLocalDateTime=" + maxLocalDateTime)
                .add("participantsSize=" + participantsSize)
                .add("id=" + id)
                .add("version=" + version)
                .add("createdBy='" + createdBy + "'")
                .add("created=" + created)
                .add("modifiedBy='" + modifiedBy + "'")
                .add("modified=" + modified)
                .add("entityStatus=" + entityStatus)
                .toString();
    }
}
