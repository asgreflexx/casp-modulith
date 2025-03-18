package casp.web.backend.dog;

import casp.web.backend.dog.data.EuropeNetState;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DogService {

    /**
     * Search for an active dog by its id.
     *
     * @param id of the Dog
     * @return a Dog with the given id, or throws an exception if not found
     */
    DogDto getDogById(UUID id);

    DogDto saveDog(DogDto dogDto);

    void deleteDogById(UUID id);

    Optional<DogDto> getDogByChipNumber(String chipNumber);

    Page<DogDto> getDogsByNameOrOwnerName(String name, String ownerName, Pageable pageable);

    Page<DogDto> getDogs(@Nullable String value, Pageable pageable);

    /**
     * Get dogs that were not checked.
     * A dog is not checked, if its EuropeNet state is neither {@link EuropeNetState#DOG_IS_REGISTERED}
     * nor {@link EuropeNetState#DOG_NOT_REGISTERED}  and its chip number isn't empty.
     *
     * @return a page of dogs that were not checked.
     */
    Page<DogDto> getDogsThatWereNotChecked(@Nullable Pageable pageable);
}
