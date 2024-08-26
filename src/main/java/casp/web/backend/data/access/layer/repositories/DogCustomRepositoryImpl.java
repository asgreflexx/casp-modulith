package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.dog.QDog;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.EuropeNetState;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        var query = new SpringDataMongodbQuery<>(mongoOperations, Dog.class);
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
        return query.where(expression)
                .orderBy(dog.name.asc(), dog.ownerName.asc())
                .fetch();
    }

    @Override
    public Set<Dog> findAllByEuropeNetStateNotChecked() {
        var query = new SpringDataMongodbQuery<>(mongoOperations, Dog.class);
        var expression = dog.entityStatus.eq(EntityStatus.ACTIVE)
                .and(dog.chipNumber.isNotEmpty())
                .and(dog.europeNetState.eq(EuropeNetState.NOT_CHECKED));

        return query.where(expression).stream().collect(Collectors.toSet());
    }
}
