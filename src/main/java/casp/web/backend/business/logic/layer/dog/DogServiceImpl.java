package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.data.access.layer.dog.DogRepository;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
class DogServiceImpl implements DogService {
    private static final Logger LOG = LoggerFactory.getLogger(DogServiceImpl.class);

    private final DogRepository dogRepository;
    private final DogHasHandlerService dogHasHandlerService;

    @Autowired
    DogServiceImpl(final DogRepository dogRepository, final DogHasHandlerService dogHasHandlerService) {
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
    public void saveDog(final Dog dog) {
        dogRepository.save(dog);
    }

    @Override
    public void deleteDogById(final UUID id) {
        var dog = getDogById(id);
        dogHasHandlerService.deleteDogHasHandlersByDogId(id);
        dog.setEntityStatus(EntityStatus.DELETED);
        dogRepository.save(dog);
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
