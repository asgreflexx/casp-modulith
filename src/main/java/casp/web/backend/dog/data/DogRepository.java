package casp.web.backend.dog.data;

import casp.web.backend.common.base.BaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;

/**
 * IDogRepository
 *
 * @author sarah
 */
@JaversSpringDataAuditable
public interface DogRepository extends BaseRepository<Dog>, DogCustomRepository {
}
