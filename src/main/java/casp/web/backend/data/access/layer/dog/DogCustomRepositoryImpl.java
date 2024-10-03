package casp.web.backend.data.access.layer.dog;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.enumerations.EuropeNetState;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class DogCustomRepositoryImpl implements DogCustomRepository {
    private final QDog dog;
    private final MongoOperations mongoOperations;

    @Autowired
    DogCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
        dog = QDog.dog;
    }

    @Override
    public List<Dog> findAllByChipNumberOrDogNameOrOwnerName(final String chipNumber, final String dogName, final String ownerName) {
        var expression = dog.entityStatus.eq(EntityStatus.ACTIVE);
        if (StringUtils.isNotBlank(chipNumber)) {
            expression = expression.and(dog.chipNumber.equalsIgnoreCase(chipNumber));
        } else {
            if (StringUtils.isNotBlank(dogName)) {
                expression = expression.and(dog.name.equalsIgnoreCase(dogName));
            }
            if (StringUtils.isNotBlank(ownerName)) {
                expression = expression.and(dog.ownerName.equalsIgnoreCase(ownerName));
            }
        }
        return createQuery().where(expression)
                .orderBy(dog.name.asc(), dog.ownerName.asc())
                .fetch();
    }

    @Override
    public Page<Dog> findAllByEuropeNetStateNotChecked(final Pageable pageable) {
        var expression = dog.entityStatus.eq(EntityStatus.ACTIVE)
                .and(dog.chipNumber.isNotEmpty())
                .and(dog.europeNetState.ne(EuropeNetState.DOG_IS_REGISTERED));

        return createQuery().where(expression).fetchPage(pageable);
    }

    private SpringDataMongodbQuery<Dog> createQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Dog.class);
    }
}
