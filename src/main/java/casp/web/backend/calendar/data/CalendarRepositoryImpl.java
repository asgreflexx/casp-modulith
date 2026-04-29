package casp.web.backend.calendar.data;

import casp.web.backend.common.enums.EntityStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.UnionWithOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
class CalendarRepositoryImpl implements CalendarRepository {
    // cf. casp.web.backend.common.base.BaseDocument.entityStatus
    private static final String ENTITY_STATUS_FIELD = "entityStatus";
    // cf. casp.web.backend.calendar.data.BaseEvent.calendarEntries.entryFromODT
    private static final String FROM_FIELD = "calendarEntries.entryFromODT";
    // cf. casp.web.backend.calendar.data.BaseEvent.calendarEntries.entryToODT
    private static final String TO_FIELD = "calendarEntries.entryToODT";
    // cf. casp.web.backend.calendar.data.BaseEvent.calendarEntries
    private static final String CALENDAR_ENTRIES_FIELD = "calendarEntries";
    // cf. casp.web.backend.common.base.BaseDocument.id
    private static final String ID_FIELD = "id";
    // cf. casp.web.backend.calendar.data.BaseEvent.eventType
    private static final String EVENT_TYPE_FIELD = "eventType";
    // cf. casp.web.backend.calendar.data.BaseEvent.name
    private static final String NAME_FIELD = "name";
    // cf. casp.web.backend.calendar.data.Course
    private static final String COURSE_COLLECTION = "course";
    // cf. casp.web.backend.calendar.data.Exam
    private static final String EXAM_COLLECTION = "exam";
    // cf. casp.web.backend.calendar.data.Event
    private static final String EVENT_COLLECTION = "event";
    // cf. casp.web.backend.calendar.data.BaseEvent.member.id
    private static final String MEMBER_ID_FIELD = "member.$id";

    private final MongoOperations mongoOperations;

    CalendarRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public List<CalendarEntryProjection> findCalendarEntriesByFromAndToAndMemberId(OffsetDateTime from, OffsetDateTime to, @Nullable UUID memberId) {
        var criteria = Criteria.where(ENTITY_STATUS_FIELD).is(EntityStatus.ACTIVE)
                .and(FROM_FIELD).gte(from)
                .and(TO_FIELD).lte(to);
        if (memberId != null) {
            criteria = criteria.and(MEMBER_ID_FIELD).is(memberId);
        }
        var matchOperation = Aggregation.match(criteria);

        var examUnionOperation = UnionWithOperation.unionWith(EXAM_COLLECTION)
                .pipeline(matchOperation);
        var eventUnionOperation = UnionWithOperation.unionWith(EVENT_COLLECTION)
                .pipeline(matchOperation);

        var aggregation = Aggregation.newAggregation(
                matchOperation,
                examUnionOperation,
                eventUnionOperation,
                Aggregation.project(ID_FIELD, CALENDAR_ENTRIES_FIELD, EVENT_TYPE_FIELD, NAME_FIELD),
                Aggregation.sort(Sort.Direction.ASC, FROM_FIELD, TO_FIELD)
        );
        return mongoOperations
                .aggregate(aggregation, COURSE_COLLECTION, CalendarEntryProjection.class)
                .getMappedResults();
    }
}
