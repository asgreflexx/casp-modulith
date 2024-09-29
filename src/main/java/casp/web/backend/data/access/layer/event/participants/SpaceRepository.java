package casp.web.backend.data.access.layer.event.participants;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface SpaceRepository extends MongoRepository<Space, UUID>, QuerydslPredicateExecutor<Space>, SpaceCustomRepository {
}
