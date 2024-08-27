package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import casp.web.backend.data.access.layer.documents.dog.QDogHasHandler;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.member.Member;
import casp.web.backend.data.access.layer.documents.member.QMember;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class DogHasHandlerCustomRepositoryImpl implements DogHasHandlerCustomRepository {
    private final MongoOperations mongoOperations;
    private final DogRepository dogRepository;

    @Autowired
    DogHasHandlerCustomRepositoryImpl(final MongoOperations mongoOperations, final DogRepository dogRepository) {
        this.mongoOperations = mongoOperations;
        this.dogRepository = dogRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Set<DogHasHandler> findAllByMemberNameOrDogName(final String name) {
        var query = new SpringDataMongodbQuery<>(mongoOperations, DogHasHandler.class);
        var dogHasHandler = QDogHasHandler.dogHasHandler;
        var expression = dogHasHandler.entityStatus.eq(EntityStatus.ACTIVE);
        if (ObjectUtils.isNotEmpty(name)) {
            expression = expression.and(dogHasHandler.memberId.in(getMemberIds(name)).or(dogHasHandler.dogId.in(getDogIds(name))));
        }
        return query.where(expression).stream().collect(Collectors.toSet());
    }

    private Set<UUID> getDogIds(final String name) {
        return dogRepository.findAllByEntityStatusAndNameIgnoreCase(EntityStatus.ACTIVE, name)
                .stream()
                .map(Dog::getId)
                .collect(Collectors.toSet());
    }

    private Set<UUID> getMemberIds(final String name) {
        var query = new SpringDataMongodbQuery<>(mongoOperations, Member.class);
        var member = QMember.member;
        var expression = member.entityStatus.eq(EntityStatus.ACTIVE)
                .and(member.firstName.equalsIgnoreCase(name).or(member.lastName.equalsIgnoreCase(name)));
        return query.where(expression).stream().map(Member::getId).collect(Collectors.toSet());
    }
}
