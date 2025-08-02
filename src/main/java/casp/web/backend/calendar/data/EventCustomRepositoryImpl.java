package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.EventParticipant;
import casp.web.backend.common.reference.MemberReference;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Component
class EventCustomRepositoryImpl extends BaseEventCustomRepositoryImpl<Event> implements EventCustomRepository {
    private static final QEvent EVENT = QEvent.event;

    @Autowired
    EventCustomRepositoryImpl(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    @Override
    public Stream<Event> findAllBetweenFromAndToOrMemberId(LocalDateTime from, LocalDateTime to, @Nullable UUID memberId) {
        var criteria = createTimeRangeCriteria(from, to);
        if (memberId != null) {
            criteria = criteria.and(EVENT.member.id.eq(memberId)
                    .or(EVENT.participants.contains(mapToParticipant(memberId))));
        }
        return query()
                .where(criteria)
                .stream();
    }

    private EventParticipant mapToParticipant(UUID memberId) {
        var memberReference = new MemberReference();
        memberReference.setId(memberId);
        return new EventParticipant(memberReference);
    }
}
