package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class EventCustomRepositoryImpl implements EventCustomRepository {
    private static final QEvent EVENT = QEvent.event;
    private final MongoOperations mongoOperations;

    @Autowired
    EventCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Set<Event> findAllByMemberIdAndNotDeleted(final UUID memberId) {
        var expression = EVENT.entityStatus.ne(EntityStatus.DELETED)
                .and(EVENT.member.id.eq(memberId));
        return getEventSet(expression);
    }

    @Override
    public Set<Event> findAllByMemberIdAndStatus(final UUID memberId, final EntityStatus entityStatus) {
        var expression = EVENT.entityStatus.eq(entityStatus)
                .and(EVENT.member.id.eq(memberId));
        return getEventSet(expression);
    }

    private Set<Event> getEventSet(final BooleanExpression expression) {
        return query()
                .where(expression)
                .stream()
                .collect(Collectors.toSet());
    }

    private SpringDataMongodbQuery<Event> query() {
        return new SpringDataMongodbQuery<>(mongoOperations, Event.class);
    }
}
