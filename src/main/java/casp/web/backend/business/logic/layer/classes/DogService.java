package casp.web.backend.business.logic.layer.classes;

import casp.web.backend.business.logic.layer.interfaces.IDogHasHandlerService;
import casp.web.backend.business.logic.layer.interfaces.IDogService;
import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.repositories.IDogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * DogService
 *
 * @author sarah
 */

@Service
public class DogService implements IDogService {

    private final IDogRepository iDogRepository;
    private final IDogHasHandlerService iDogHasHandlerService;

    @Autowired
    DogService(IDogRepository iDogRepository, IDogHasHandlerService iDogHasHandlerService) {
        this.iDogRepository = iDogRepository;
        this.iDogHasHandlerService = iDogHasHandlerService;
    }

    @Transactional(readOnly = true)
    @Override
    public Dog getDogById(UUID id) throws Exception {

        return iDogRepository.findDogByEntityStatusAndId(EntityStatus.ACTIVE, id).orElseThrow(() -> new Exception("Dog not found.")); // findById ist schon festgelegt
    }

    @Override
    public Dog saveDog(Dog dog) {

        return iDogRepository.save(dog);
    }

    @Override
    public void deleteDogById(UUID id) throws Exception {

        Dog dog = iDogRepository.findById(id).orElseThrow(() -> new Exception("Dog not found."));
        dog.setEntityStatus(EntityStatus.DELETED);
        iDogHasHandlerService.deleteDogHasHandlerByDogId(id);
        iDogRepository.save(dog);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Dog> getDogsByOwner(String ownerName, String name, String chipNumber) {

        // TODO: check if frontend is sends empty string
        if (!StringUtils.isEmpty(chipNumber)) {
            return iDogRepository.findAllByChipNumber(chipNumber);
        } else {
            return iDogRepository.findAllByOwnerNameAndName(ownerName, name);
        }
    }

    @Override
    public List<Dog> getDogs() {
        return iDogRepository.findAllByEntityStatus(EntityStatus.ACTIVE);
    }
}
