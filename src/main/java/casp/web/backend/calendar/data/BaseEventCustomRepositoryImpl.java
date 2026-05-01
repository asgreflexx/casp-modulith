package casp.web.backend.calendar.data;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.QDogHasHandlerReference;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;

import java.lang.reflect.ParameterizedType;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

abstract class BaseEventCustomRepositoryImpl<T extends BaseEvent<?>> implements BaseEventCustomRepository<T> {
    private static final QBaseEvent BASE_EVENT = QBaseEvent.baseEvent;
    private static final QDogHasHandlerReference DOG_HAS_HANDLER_REFERENCE = QDogHasHandlerReference.dogHasHandlerReference;
    @Deprecated(forRemoval = true, since = "2026-04-23")
    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Vienna");
    @Deprecated(forRemoval = true, since = "2026-04-23")
    private static final int BATCH_SIZE = 10;
    final MongoOperations mongoOperations;
    private final Class<T> baseEventClass;

    // no need to check, all classes are of type BaseEvent
    @SuppressWarnings("unchecked")
    BaseEventCustomRepositoryImpl(MongoOperations mongoOperations) {
        baseEventClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Set<T> findAllByMemberIdAndNotDeleted(UUID memberId) {
        var criteria = BASE_EVENT.entityStatus.ne(EntityStatus.DELETED)
                .and(BASE_EVENT.member.id.eq(memberId));
        return findAllByCriteria(criteria);
    }

    @Override
    public Set<T> findAllByMemberIdAndStatus(UUID memberId, EntityStatus status) {
        var criteria = BASE_EVENT.entityStatus.eq(status)
                .and(BASE_EVENT.member.id.eq(memberId));
        return findAllByCriteria(criteria);
    }

    @Override
    public boolean migrateLocaDateTimeToOffsetDateTime() {
        var criteria = BASE_EVENT.calendarEntries.any().entryFromODT.isNull();
        var page = query()
                .where(criteria)
                .fetchPage(PageRequest.ofSize(BATCH_SIZE));
        page
                .getContent()
                .forEach(this::convertCalendarEntriesToODT);
        return page.isEmpty();

    }

    @Deprecated(forRemoval = true, since = "2026-04-23")
    private void convertCalendarEntriesToODT(T t) {
        t.getCalendarEntries().forEach(calendarEntry -> {
            var entryToODT = calendarEntry.getEntryTo().atZone(ZONE_ID).toOffsetDateTime();
            var entryFromODT = calendarEntry.getEntryFrom().atZone(ZONE_ID).toOffsetDateTime();
            calendarEntry.setEntryToODT(entryToODT);
            calendarEntry.setEntryFromODT(entryFromODT);
        });
        t.setMaxTimeODT(t.getMaxTime().atZone(ZONE_ID).toOffsetDateTime());
        t.setMinTimeODT(t.getMinTime().atZone(ZONE_ID).toOffsetDateTime());
        mongoOperations.save(t);
    }

    protected SpringDataMongodbQuery<T> query() {
        return new SpringDataMongodbQuery<>(mongoOperations, baseEventClass);
    }

    Optional<UUID> findActiveDogHandlerReferenceById(UUID participantId) {
        return activeDogHasHandlerQuery(DOG_HAS_HANDLER_REFERENCE.id.eq(participantId))
                .stream()
                .findAny()
                .map(DogHasHandlerReference::getId);
    }

    private Set<T> findAllByCriteria(BooleanExpression criteria) {
        return query()
                .where(criteria)
                .stream()
                .collect(Collectors.toSet());
    }

    private SpringDataMongodbQuery<DogHasHandlerReference> dogHasHandlerQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, DogHasHandlerReference.class);
    }

    private SpringDataMongodbQuery<DogHasHandlerReference> activeDogHasHandlerQuery(BooleanExpression expression) {
        return dogHasHandlerQuery().where(DOG_HAS_HANDLER_REFERENCE.entityStatus.eq(EntityStatus.ACTIVE), expression);
    }
}
