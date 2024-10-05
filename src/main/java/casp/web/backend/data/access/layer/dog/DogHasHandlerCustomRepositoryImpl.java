package casp.web.backend.data.access.layer.dog;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.QMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class DogHasHandlerCustomRepositoryImpl implements DogHasHandlerCustomRepository {
    private static final QMember MEMBER = QMember.member;
    private static final QDogHasHandler DOG_HAS_HANDLER = QDogHasHandler.dogHasHandler;
    private static final QDog DOG = QDog.dog;
    private final MongoOperations mongoOperations;

    @Autowired
    DogHasHandlerCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Set<DogHasHandler> findAllByMemberNameOrDogName(final String name) {
        var expression = DOG_HAS_HANDLER.entityStatus.eq(EntityStatus.ACTIVE);
        if (StringUtils.hasText(name)) {
            // The use of "Join"s in MongoDb. Query DBRef with Spring Data MongoDB using QueryDSL -> It's not supported.
            // https://stackoverflow.com/a/44316748/1066054
            var dogsId = getDogsId(name);
            var membersId = getMembersId(name);
            expression = expression.and(DOG_HAS_HANDLER.dogId.in(dogsId).or(DOG_HAS_HANDLER.memberId.in(membersId)));
        }
        return createDogHasHandlerQuery()
                .where(expression)
                .stream()
                .collect(Collectors.toSet());
    }

    private List<UUID> getMembersId(final String name) {
        return new SpringDataMongodbQuery<>(mongoOperations, Member.class)
                .where(MEMBER.entityStatus.eq(EntityStatus.ACTIVE)
                        .and(MEMBER.firstName.containsIgnoreCase(name).or(MEMBER.lastName.containsIgnoreCase(name))))
                .stream()
                .map(Member::getId)
                .toList();
    }

    private List<UUID> getDogsId(final String name) {
        return new SpringDataMongodbQuery<>(mongoOperations, Dog.class)
                .where(DOG.entityStatus.eq(EntityStatus.ACTIVE)
                        .and(DOG.name.containsIgnoreCase(name)))
                .stream()
                .map(Dog::getId)
                .toList();
    }

    private SpringDataMongodbQuery<DogHasHandler> createDogHasHandlerQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, DogHasHandler.class);
    }
}
