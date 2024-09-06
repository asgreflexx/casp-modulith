package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import casp.web.backend.data.access.layer.documents.event.participant.QBaseParticipant;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class BaseParticipantCustomRepositoryImpl implements BaseParticipantCustomRepository {
    private static final QBaseParticipant BASE_PARTICIPANT = QBaseParticipant.baseParticipant;
    private final MongoOperations mongoOperations;

    @Autowired
    BaseParticipantCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }


    @Override
    public <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdIn(final Set<UUID> memberOrHandlersId, final String participantType) {
        var expression = BASE_PARTICIPANT.entityStatus.eq(EntityStatus.ACTIVE)
                .and(BASE_PARTICIPANT.memberOrHandlerId.in(memberOrHandlersId))
                .and(BASE_PARTICIPANT.participantType.eq(participantType));
        return runQueryAndMapBaseParticipants(expression);
    }

    @Override
    public <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdAndEntityStatus(final UUID memberOrHandlerId, final EntityStatus entityStatus, final String participantType) {
        var expression = BASE_PARTICIPANT.memberOrHandlerId.eq(memberOrHandlerId)
                .and(BASE_PARTICIPANT.entityStatus.eq(entityStatus))
                .and(BASE_PARTICIPANT.participantType.eq(participantType));
        return runQueryAndMapBaseParticipants(expression);
    }

    @Override
    public <P extends BaseParticipant> Set<P> findAllByMemberOrHandlerIdAndEntityStatusNot(final UUID memberOrHandlerId, final EntityStatus entityStatus, final String participantType) {
        var expression = BASE_PARTICIPANT.memberOrHandlerId.eq(memberOrHandlerId)
                .and(BASE_PARTICIPANT.entityStatus.ne(entityStatus))
                .and(BASE_PARTICIPANT.participantType.eq(participantType));
        return runQueryAndMapBaseParticipants(expression);
    }

    // The cast is correct
    @SuppressWarnings("unchecked")
    private <P extends BaseParticipant> Set<P> runQueryAndMapBaseParticipants(final BooleanExpression expression) {
        var query = new SpringDataMongodbQuery<>(mongoOperations, BaseParticipant.class);
        return query.where(expression).stream()
                .map(p -> (P) p)
                .collect(Collectors.toSet());
    }
}
