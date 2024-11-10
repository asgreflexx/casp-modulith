package casp.web.backend.dog;

import casp.web.backend.business.logic.layer.event.types.CourseService;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.dog.data.Dog;
import casp.web.backend.dog.data.DogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static casp.web.backend.dog.DogMapper.DOG_MAPPER;

@Service
class DogServiceImpl implements DogService {
    private static final Logger LOG = LoggerFactory.getLogger(DogServiceImpl.class);

    private final DogHasHandlerService dogHasHandlerService;
    private final CourseService courseService;
    private final DogRepository dogRepository;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    @Autowired
    DogServiceImpl(DogHasHandlerService dogHasHandlerService,
                   CourseService courseService,
                   DogRepository dogRepository,
                   DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        this.dogRepository = dogRepository;
        this.dogHasHandlerService = dogHasHandlerService;
        this.courseService = courseService;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
    }

    @Override
    public DogDto getDogById(UUID id) {
        return mapToDogDto(getActiveDog(id));
    }

    @Override
    public DogDto saveDog(DogDto dogDto) {
        var dog = DOG_MAPPER.toSource(dogDto);
        return mapToDogDto(dogRepository.setMetadataAndSave(dog));
    }

    @Override
    public void deleteDogById(UUID id) {
        var dog = getActiveDog(id);
        dogHasHandlerService.deleteDogHasHandlersByDogId(id);
        dog.setEntityStatus(EntityStatus.DELETED);
        dogRepository.save(dog);
    }

    @Override
    public Optional<DogDto> getDogByChipNumber(String chipNumber) {
        return dogRepository.findOneByChipNumberAndEntityStatus(chipNumber, EntityStatus.ACTIVE)
                .map(this::mapToDogDto);
    }

    @Override
    public Page<DogDto> getDogsByNameOrOwnerName(String name, String ownerName, Pageable pageable) {
        return DOG_MAPPER.toTargetPage(dogRepository.findAllByNameOrOwnerName(name, ownerName, pageable));
    }

    @Override
    public Page<DogDto> getDogs(Pageable pageable) {
        return DOG_MAPPER.toTargetPage(dogRepository.findAllByEntityStatus(EntityStatus.ACTIVE, pageable));
    }

    @Override
    public Page<DogDto> getDogsThatWereNotChecked(Pageable pageable) {
        var pageRequest = pageable != null ? pageable : Pageable.unpaged();
        return DOG_MAPPER.toTargetPage(dogRepository.findAllByEuropeNetStateNotChecked(pageRequest));
    }

    private Dog getActiveDog(UUID id) {
        return dogRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "Dog with id %s not found or it isn't active.".formatted(id);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
    }

    private DogDto mapToDogDto(Dog dog) {
        var dogDto = DOG_MAPPER.toTarget(dog);
        var dogHasHandlerSet = dogHasHandlerReferenceRepository.findAllByDogId(dog.getId());
        dogDto.setDogHasHandlerSet(DOG_MAPPER.toDogHasHandlerSet(dogHasHandlerSet));
        dogDto.setSpaces(courseService.getSpacesByDogHasHandlers(dogHasHandlerSet));
        return dogDto;
    }
}
