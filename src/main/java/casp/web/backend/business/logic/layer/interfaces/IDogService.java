package casp.web.backend.business.logic.layer.interfaces;

import casp.web.backend.data.access.layer.entities.Dog;

import java.util.List;
import java.util.UUID;

/**
 * IDogService
 *
 * @author sarah
 */

public interface IDogService {

    Dog getDogById(UUID id) throws Exception;

    Dog saveDog(Dog dog) throws Exception;

    void deleteDogById(UUID id) throws Exception;

    List<Dog> getDogsByOwner(String ownerName, String name, String chipNumber);

    List<Dog> getDogs();

}
