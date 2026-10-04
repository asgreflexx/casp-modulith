package casp.web.backend.dog.data;

import casp.web.backend.common.enums.EntityStatus;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Repository;

@Repository
class DogCustomRepositoryImpl implements DogCustomRepository {
    private final QDog dog;
    private final MongoOperations mongoOperations;

    @Autowired
    DogCustomRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
        dog = QDog.dog;
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

    private SpringDataMongodbQuery<Dog> createQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Dog.class);
    }
}
