package casp.web.backend.dog.data;

import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * IDogRepository
 *
 * @author sarah
 */

public interface DogRepository extends BaseRepository<Dog>, DogCustomRepository {

    Optional<Dog> findOneByChipNumberAndEntityStatus(String chipNumber, EntityStatus entityStatus);

    Page<Dog> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);
}
