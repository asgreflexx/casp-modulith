package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.entities.Card;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface ICardRepository extends MongoRepository<Card, UUID> {

    List<Card> findAllByMemberIdAndEntityStatus(UUID memberId, EntityStatus entityStatus);
}
