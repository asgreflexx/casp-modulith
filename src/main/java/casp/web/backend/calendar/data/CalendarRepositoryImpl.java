package casp.web.backend.calendar.data;

import casp.web.backend.common.enums.EntityStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.UnionWithOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static casp.web.backend.calendar.data.BaseEvent.MAX_TIME_FIELD;
import static casp.web.backend.calendar.data.BaseEvent.MIN_TIME_FIELD;

@Repository
class CalendarRepositoryImpl implements CalendarRepository {
    // cf. casp.web.backend.common.base.BaseDocument
    private static final String ENTITY_STATUS_FIELD = "entityStatus";
    private static final String ID_FIELD = "id";

    // cf. casp.web.backend.calendar.data.BaseEvent
    private static final String CALENDAR_ENTRIES_FIELD = "calendarEntries";
    private static final String EVENT_TYPE_FIELD = "eventType";
    private static final String NAME_FIELD = "name";
    private static final String MEMBER_ID_FIELD = "member.$id";
    private static final String PARTICIPANTS_PREFIX = "participants.";

    // cf. casp.web.backend.calendar.data.Course
    private static final String COURSE_COLLECTION = "course";
    private static final String CO_TRAINER_ID_FIELD = "coTrainers." + MEMBER_ID_FIELD;

    // cf. casp.web.backend.calendar.data.Exam
    private static final String EXAM_COLLECTION = "exam";

    // cf. casp.web.backend.calendar.data.Event
    private static final String EVENT_COLLECTION = "event";
    private static final String BASE_EVENT_PARTICIPANTS_FIELD = PARTICIPANTS_PREFIX + MEMBER_ID_FIELD;

    // cf. Exam & Course
    private static final String DOG_HAS_HANDLER_FIELD = "dogHasHandler";
    private static final String DOG_HAS_HANDLER_ID_FIELD = PARTICIPANTS_PREFIX + DOG_HAS_HANDLER_FIELD + ".$id";
    private static final String RESOLVED_DOG_HAS_HANDLER = "resolvedDogHasHandler";
    private static final String RESOLVED_MEMBER_ID_FIELD = RESOLVED_DOG_HAS_HANDLER + "." + MEMBER_ID_FIELD;
    private static final String COLLECTION_ID = "_id";

    private final MongoOperations mongoOperations;

    CalendarRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public List<CalendarEntryProjection> findCalendarEntriesByFromAndToAndMemberId(OffsetDateTime from, OffsetDateTime to, @Nullable UUID memberId) {
        var pipeline = new ArrayList<AggregationOperation>();
        var criteria = initializeCriteria(from, to);

        processCriteriaWithMemberId(criteria, memberId, pipeline);
        mergeExamAndEventPipelines(pipeline);

        pipeline.add(Aggregation.project(ID_FIELD,
                CALENDAR_ENTRIES_FIELD,
                EVENT_TYPE_FIELD,
                NAME_FIELD,
                MIN_TIME_FIELD,
                MAX_TIME_FIELD));
        pipeline.add(Aggregation.sort(Sort.Direction.ASC, MIN_TIME_FIELD, MAX_TIME_FIELD));

        return mongoOperations
                .aggregate(Aggregation.newAggregation(pipeline), COURSE_COLLECTION, CalendarEntryProjection.class)
                .getMappedResults();
    }

    private static void mergeExamAndEventPipelines(ArrayList<AggregationOperation> pipeline) {
        var examUnionOperation = UnionWithOperation.unionWith(EXAM_COLLECTION)
                .pipeline(pipeline);
        var eventUnionOperation = UnionWithOperation.unionWith(EVENT_COLLECTION)
                .pipeline(pipeline);
        pipeline.add(examUnionOperation);
        pipeline.add(eventUnionOperation);
    }

    private static void processCriteriaWithMemberId(Criteria criteria, @Nullable UUID memberId, ArrayList<AggregationOperation> pipeline) {
        if (memberId == null) {
            pipeline.add(Aggregation.match(criteria));
        } else {
            var lookup = Aggregation.lookup(DOG_HAS_HANDLER_FIELD, DOG_HAS_HANDLER_ID_FIELD, COLLECTION_ID, RESOLVED_DOG_HAS_HANDLER);
            pipeline.add(lookup);
            criteria.andOperator(new Criteria().orOperator(
                    Criteria.where(MEMBER_ID_FIELD).is(memberId),
                    Criteria.where(BASE_EVENT_PARTICIPANTS_FIELD).is(memberId),
                    Criteria.where(RESOLVED_MEMBER_ID_FIELD).is(memberId),
                    Criteria.where(CO_TRAINER_ID_FIELD).is(memberId)
            ));
            pipeline.add(Aggregation.match(criteria));
        }
    }

    private static Criteria initializeCriteria(OffsetDateTime from, OffsetDateTime to) {
        return Criteria.where(ENTITY_STATUS_FIELD).is(EntityStatus.ACTIVE)
                .and(MAX_TIME_FIELD).gte(from)
                .and(MIN_TIME_FIELD).lte(to);
    }
}
