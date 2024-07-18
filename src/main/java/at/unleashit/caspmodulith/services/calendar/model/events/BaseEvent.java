package at.unleashit.caspmodulith.services.calendar.model.events;

import at.unleashit.caspmodulith.services.calendar.model.configurations.BaseEntity;
import at.unleashit.caspmodulith.services.calendar.model.configurations.TypesRegex;
import at.unleashit.caspmodulith.services.calendar.model.options.DailyEventOption;
import at.unleashit.caspmodulith.services.calendar.model.options.WeeklyEventOption;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;


@BaseEventOptionConstraint
@QueryEntity
@Document(BaseEvent.COLLECTION)
public class BaseEvent extends BaseEntity {
    public static final String COLLECTION = "BASE_EVENT";

    @NotNull
    @Pattern(regexp = TypesRegex.BASE_EVENT_TYPES_REGEX)
    protected String eventType;

    @NotBlank
    protected String name;

    protected String description;

    @NotNull
    protected UUID memberId;

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

    public void setMinAndMaxEventPeriod(List<Calendar> entries) {
        if (!CollectionUtils.isEmpty(entries)) {
            final Calendar min = entries
                    .stream()
                    .min(Comparator.comparing(Calendar::getEventFrom))
                    .orElseThrow(NoSuchElementException::new);

            final Calendar max = entries
                    .stream()
                    .max(Comparator.comparing(Calendar::getEventTo))
                    .orElseThrow(NoSuchElementException::new);

            minLocalDateTime = min.getEventFrom();
            maxLocalDateTime = max.getEventTo();
        }
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
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
