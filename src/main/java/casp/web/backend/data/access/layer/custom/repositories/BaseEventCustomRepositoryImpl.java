package casp.web.backend.data.access.layer.custom.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.QBaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
class BaseEventCustomRepositoryImpl implements BaseEventCustomRepository {
    private static final QBaseEvent BASE_EVENT = QBaseEvent.baseEvent;
    private final MongoOperations mongoOperations;

    @Autowired
    BaseEventCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Page<BaseEvent> findAllByYearAndEventType(final int year, final String eventType, final Pageable pageable) {
        var from = LocalDateTime.of(LocalDate.of(year, 1, 1), LocalTime.MIN);
        var to = LocalDateTime.of(LocalDate.of(year, 12, 31), LocalTime.MAX);
        var query = new SpringDataMongodbQuery<>(mongoOperations, BaseEvent.class);

        var expression = BASE_EVENT.entityStatus.eq(EntityStatus.ACTIVE)
                .and(BASE_EVENT.minLocalDateTime.goe(from))
                .and(BASE_EVENT.maxLocalDateTime.loe(to))
                .and(BASE_EVENT.eventType.eq(eventType));

        return query.where(expression).fetchPage(pageable);
    }
}
