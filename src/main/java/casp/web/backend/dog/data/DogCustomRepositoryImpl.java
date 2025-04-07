package casp.web.backend.dog.data;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class DogCustomRepositoryImpl implements DogCustomRepository {
    private final QDog dog;
    private final MongoOperations mongoOperations;
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    @Autowired
    DogCustomRepositoryImpl(MongoOperations mongoOperations, DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        this.mongoOperations = mongoOperations;
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
        dog = QDog.dog;
    }

    @Override
    public Page<Dog> findAllByNameOrOwnerName(String dogName, String ownerName, Pageable pageable) {
        var expression = dog.entityStatus.eq(EntityStatus.ACTIVE);
        if (StringUtils.isNotBlank(dogName)) {
            expression = expression.and(dog.name.equalsIgnoreCase(dogName));
        }
        if (StringUtils.isNotBlank(ownerName)) {
            expression = expression.and(dog.ownerName.equalsIgnoreCase(ownerName));
        }
        return createQuery().where(expression)
                .fetchPage(pageable);
    }

    @Override
    public Page<Dog> findAllByEuropeNetStateNotChecked(Pageable pageable) {
        var expression = dog.entityStatus.eq(EntityStatus.ACTIVE)
                .and(dog.chipNumber.isNotNull().and(dog.chipNumber.isNotEmpty()))
                .and(dog.europeNetState.notIn(EuropeNetState.DOG_NOT_REGISTERED, EuropeNetState.DOG_IS_REGISTERED));

        return createQuery().where(expression).fetchPage(pageable);
    }

    @Override
    public Page<Dog> findAllByValue(@Nullable String value, Pageable pageable) {
        var expression = dog.entityStatus.eq(EntityStatus.ACTIVE);
        if (StringUtils.isNotBlank(value)) {
            expression = expression.and(dog.name.containsIgnoreCase(value)
                    .or(dog.ownerName.containsIgnoreCase(value))
                    .or(dog.chipNumber.containsIgnoreCase(value)));
        }
        return createQuery().where(expression).fetchPage(pageable);
    }

    @Override
    public Page<Dog> findAllByNotMemberId(UUID memberId, String dogName, Pageable pageable) {
        var expression = dog.entityStatus.eq(EntityStatus.ACTIVE)
                .and(dog.id.notIn(getDogIdsRelatedToThisMember(memberId)));
        if (StringUtils.isNotBlank(dogName)) {
            expression = expression.and(dog.name.containsIgnoreCase(dogName));
        }
        return createQuery().where(expression).fetchPage(pageable);
    }

    private List<UUID> getDogIdsRelatedToThisMember(UUID memberId) {
        return dogHasHandlerReferenceRepository.findAllByMemberId(memberId)
                .stream().map(dhh -> dhh.getDog().getId())
                .toList();
    }

    private SpringDataMongodbQuery<Dog> createQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Dog.class);
    }
}
