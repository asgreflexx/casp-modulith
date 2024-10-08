package casp.web.backend.data.access.layer.event.types;


import casp.web.backend.data.access.layer.commons.BaseDocument;
import casp.web.backend.data.access.layer.event.TypesRegex;
import casp.web.backend.data.access.layer.event.options.DailyEventOption;
import casp.web.backend.data.access.layer.event.options.WeeklyEventOption;
import casp.web.backend.data.access.layer.member.Member;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

// cannot be abstract because of error: abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information
@BaseEventOptionConstraint
@QueryEntity
@Document(BaseEvent.COLLECTION)
public class BaseEvent extends BaseDocument {
    static final String COLLECTION = "BASE_EVENT";

    @NotNull
    @Pattern(regexp = TypesRegex.BASE_EVENT_TYPES_REGEX)
    protected String eventType;

    @NotBlank
    protected String name;

    protected String description;

    @NotNull
    protected UUID memberId;

    @Valid
    @DBRef
    protected Member member;

    @Valid
    protected DailyEventOption dailyOption;

    @Valid
    protected WeeklyEventOption weeklyOption;

    // These fields aren't set by the user
    protected LocalDateTime minLocalDateTime;
    protected LocalDateTime maxLocalDateTime;
    protected int participantsSize = 0;

    // needed for deserialization
    public BaseEvent() {
    }

    protected BaseEvent(String eventType) {
        this.eventType = eventType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID member) {
        this.memberId = member;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(final Member member) {
        this.member = member;
    }

    public DailyEventOption getDailyOption() {
        return dailyOption;
    }

    public void setDailyOption(DailyEventOption option) {
        this.dailyOption = option;
    }

    public WeeklyEventOption getWeeklyOption() {
        return weeklyOption;
    }

    public void setWeeklyOption(WeeklyEventOption weeklyOption) {
        this.weeklyOption = weeklyOption;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getMinLocalDateTime() {
        return minLocalDateTime;
    }

    public void setMinLocalDateTime(LocalDateTime minLocalDateTime) {
        this.minLocalDateTime = minLocalDateTime;
    }

    public LocalDateTime getMaxLocalDateTime() {
        return maxLocalDateTime;
    }

    public void setMaxLocalDateTime(LocalDateTime maxLocalDateTime) {
        this.maxLocalDateTime = maxLocalDateTime;
    }

    public int getParticipantsSize() {
        return participantsSize;
    }

    public void setParticipantsSize(int participantsSize) {
        this.participantsSize = participantsSize;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
