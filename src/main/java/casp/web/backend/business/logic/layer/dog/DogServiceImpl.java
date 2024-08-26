package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.repositories.DogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

/**
 * DogService
 *
 * @author sarah
 */

@Service
class DogServiceImpl implements DogService {
    private static final Logger LOG = LoggerFactory.getLogger(DogServiceImpl.class);

    private final DogRepository dogRepository;
    private final DogHasHandlerService dogHasHandlerService;

    @Autowired
    DogServiceImpl(DogRepository dogRepository, DogHasHandlerService dogHasHandlerService) {
        this.dogRepository = dogRepository;
        this.dogHasHandlerService = dogHasHandlerService;
    }

    @Override
    public Dog getDogById(final UUID id) {
        return dogRepository.findDogByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "Dog with id %s not found or it isn't active.".formatted(id);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
    }

    @Override
    public Dog saveDog(final Dog dog) {
        return dogRepository.save(dog);
    }

    @Transactional
    @Override
    public void deleteDogById(final UUID id) {
        dogRepository.findDogByIdAndEntityStatusIsNot(id, EntityStatus.DELETED)
                .ifPresent(dog -> {
                    dog.setEntityStatus(EntityStatus.DELETED);
                    dogHasHandlerService.deleteDogHasHandlerByDogId(id);
                });
    }

    @Override
    public List<Dog> getDogsByOwnerNameAndDogsNameOrChipNumber(final String chipNumber, final String name, final String ownerName) {
        return dogRepository.findAllByChipNumberOrDogNameOrOwnerName(chipNumber, name, ownerName);
    }

    @Override
    public Page<Dog> getDogs(final Pageable pageable) {
        return dogRepository.findAllByEntityStatus(EntityStatus.ACTIVE, pageable);
    }

    @Override
    public Set<Dog> getDogsThatWereNotChecked() {
        return dogRepository.findAllByEuropeNetStateNotChecked();
    }
}
