package casp.web.backend.dog.data;

import casp.web.backend.common.enums.EntityStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

@Component
class DogCustomRepositoryImpl implements DogCustomRepository {
    private final QDog dog;
    private final MongoOperations mongoOperations;

    @Autowired
    DogCustomRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
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

    private SpringDataMongodbQuery<Dog> createQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Dog.class);
    }
}
