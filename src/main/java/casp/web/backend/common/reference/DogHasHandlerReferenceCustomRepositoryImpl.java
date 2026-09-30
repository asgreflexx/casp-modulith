package casp.web.backend.common.reference;

import casp.web.backend.common.enums.EntityStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
class DogHasHandlerReferenceCustomRepositoryImpl implements DogHasHandlerReferenceCustomRepository {
    private static final QDogHasHandlerReference DOG_HAS_HANDLER_REFERENCE = QDogHasHandlerReference.dogHasHandlerReference;
    private static final BooleanExpression DOG_HAS_HANDLER_IS_ACTIVE = DOG_HAS_HANDLER_REFERENCE.entityStatus.eq(EntityStatus.ACTIVE);
    private final MongoOperations mongoOperations;

    @Autowired
    DogHasHandlerReferenceCustomRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Set<DogHasHandlerReference> findAllByMemberId(UUID memberId) {
        var memberCriteria = DOG_HAS_HANDLER_REFERENCE.member.id.eq(memberId);
        return findAllByCriteria(memberCriteria);
    }

    @Override
    public Set<DogHasHandlerReference> findAllByDogId(UUID dogId) {
        var dogCriteria = DOG_HAS_HANDLER_REFERENCE.dog.id.eq(dogId);
        return findAllByCriteria(dogCriteria);
    }

    private SpringDataMongodbQuery<DogHasHandlerReference> query() {
        return new SpringDataMongodbQuery<>(mongoOperations, DogHasHandlerReference.class);
    }

    private Set<DogHasHandlerReference> findAllByCriteria(BooleanExpression criteria) {
        return query()
                .where(criteria.and(DOG_HAS_HANDLER_IS_ACTIVE))
                .stream()
                .filter(DogHasHandlerReference::isActive)
                .collect(Collectors.toSet());
    }
}
