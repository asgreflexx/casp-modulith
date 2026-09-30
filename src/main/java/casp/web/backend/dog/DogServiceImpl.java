package casp.web.backend.dog;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.dog.data.Dog;
import casp.web.backend.dog.data.DogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

import static casp.web.backend.dog.DogMapper.DOG_MAPPER;

@RequiredArgsConstructor
@Slf4j
@Service
class DogServiceImpl implements DogService {
    private final DogHasHandlerService dogHasHandlerService;
    private final DogRepository dogRepository;

    @Override
    public DogDto getDogById(UUID id) {
        return DOG_MAPPER.toTarget(getActiveDog(id));
    }

    @Override
    public DogDto saveDog(DogDto dogDto) {
        var dog = DOG_MAPPER.toSource(dogDto);
        return DOG_MAPPER.toTarget(dogRepository.save(dog));
    }

    @Override
    public void deleteDogById(UUID id) {
        var dog = getActiveDog(id);
        dogHasHandlerService.deleteDogHasHandlersByDogId(id);
        dog.setEntityStatus(EntityStatus.DELETED);
        dogRepository.save(dog);
    }

    @Override
    public Page<DogDto> getDogs(String value, Pageable pageable) {
        return DOG_MAPPER.toTargetPage(dogRepository.findAllByValue(value, pageable));
    }

    @Override
    public Page<DogDto> getDogsThatWereNotChecked(Pageable pageable) {
        return DOG_MAPPER.toTargetPage(dogRepository.findAllByEuropeNetStateNotChecked(pageable));
    }

    private Dog getActiveDog(UUID id) {
        return dogRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "Dog with id %s not found or it isn't active.".formatted(id);
            log.error(msg);
            return new NoSuchElementException(msg);
        });
    }
}
