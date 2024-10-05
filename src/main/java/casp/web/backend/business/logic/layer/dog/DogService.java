package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.dog.Dog;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DogService {

    /**
     * Search for an active dog by its id.
     *
     * @param id of the Dog
     * @return a Dog with the given id, or throws an exception if not found
     */
    Dog getDogById(UUID id);

    void saveDog(Dog dog);

    void deleteDogById(UUID id);

    /**
     * Get all dogs that match the given criteria:
     * 1. Either ownerName and name are empty or chip number is empty.
     * 2. If chip number is empty, ownerName and name should not be empty.
     *
     * @param chipNumber the chip number of the dog
     * @param name       the name of the dog
     * @param ownerName  the name of the owner
     * @return a list of dogs that match the given criteria, or an empty list if none found.
     */
    List<Dog> getDogsByOwnerNameAndDogsNameOrChipNumber(@Nullable String chipNumber, @Nullable String name, @Nullable String ownerName);

    Page<Dog> getDogs(Pageable pageable);

    /**
     * Get all dogs that were not checked.
     * A dog is not checked, if its EuropeNet state is not checked and its chip number isn't empty.
     *
     * @return a page of dogs that were not checked.
     */
    Page<Dog> getDogsThatWereNotChecked(final @Nullable Pageable pageable);
}
