package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * IDogService
 *
 * @author sarah
 */

public interface DogService {

    Dog getDogById(UUID id);

    void saveDog(Dog dog);

    void deleteDogById(UUID id);
    /**
    * either ownerName and name are empty or chip number is empty.
    * If chip number is empty, ownerName and name should not be empty.
    */
    List<Dog> getDogsByOwnerNameAndDogsNameOrChipNumber(@Nullable String chipNumber, @Nullable String name, @Nullable String ownerName);

    Page<Dog> getDogs(Pageable pageable);

    Set<Dog> getDogsThatWereNotChecked();
}
