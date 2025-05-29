package casp.web.backend.dog.data;

import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import java.util.Optional;

/**
 * IDogRepository
 *
 * @author sarah
 */
@JaversSpringDataAuditable
public interface DogRepository extends BaseRepository<Dog>, DogCustomRepository {

    Optional<Dog> findOneByChipNumberAndEntityStatus(String chipNumber, EntityStatus entityStatus);
}
