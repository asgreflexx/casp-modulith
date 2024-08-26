package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import casp.web.backend.data.access.layer.documents.dog.QDogHasHandler;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.member.Member;
import casp.web.backend.data.access.layer.documents.member.QMember;
import casp.web.backend.data.access.layer.repositories.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.DogRepository;
import casp.web.backend.data.access.layer.repositories.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * DogHasHandlerService
 *
 * @author sarah
 */

@Service
class DogHasHandlerServiceImpl implements DogHasHandlerService {
    private static final Logger LOG = LoggerFactory.getLogger(DogHasHandlerServiceImpl.class);

    private final DogHasHandlerRepository dogHasHandlerRepository;
    private final MemberRepository memberRepository;
    private final DogRepository dogRepository;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    DogHasHandlerServiceImpl(final DogHasHandlerRepository dogHasHandlerRepository,
                             final MemberRepository memberRepository,
                             final DogRepository dogRepository,
                             final MongoTransactionManager mongoTransactionManager) {
        this.dogHasHandlerRepository = dogHasHandlerRepository;
        this.memberRepository = memberRepository;
        this.dogRepository = dogRepository;
        this.transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    }

    @Transactional
    @Override
    public DogHasHandler saveDogHasHandler(final DogHasHandler dogHasHandler) {
        setDogAndMember(dogHasHandler);
        return dogHasHandlerRepository.save(dogHasHandler);
    }

    @Override
    public DogHasHandler getDogHasHandlerById(final UUID id) {
        return dogHasHandlerRepository.findDogHasHandlerByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "DogHasHandler with id %s not found or it isn't active".formatted(id);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
    }

    @Transactional
    @Override
    public void deleteDogHasHandlerByMemberId(final UUID memberId) {
        getHandlersByMemberId(memberId).forEach(dh -> dh.setEntityStatus(EntityStatus.DELETED));
    }

    @Transactional
    @Override
    public void deleteDogHasHandlerByDogId(final UUID dogId) {
        getHandlersByDogId(dogId).forEach(dh -> dh.setEntityStatus(EntityStatus.DELETED));
    }

    @Transactional
    @Override
    public Set<Dog> getDogsByMemberId(final UUID memberId) {
        return getHandlersByMemberId(memberId).stream()
                .map(dh -> setDogAndMemberIfTheyAreNull(dh).getDog())
                .collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public Set<Member> getMembersByDogId(final UUID dogId) {
        return getHandlersByDogId(dogId).stream()
                .map(dh -> setDogAndMemberIfTheyAreNull(dh).getMember())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<DogHasHandler> getHandlersByMemberId(final UUID memberId) {
        return dogHasHandlerRepository.findAllByEntityStatusAndMemberId(EntityStatus.ACTIVE, memberId);
    }

    @Override
    public Set<DogHasHandler> getHandlersByDogId(final UUID dogId) {
        return dogHasHandlerRepository.findAllByEntityStatusAndDogId(EntityStatus.ACTIVE, dogId);
    }

    @Transactional
    @Override
    public Set<DogHasHandler> searchDogHasHandlerByFirstNameOrLastNameOrDogName(final String name) {
        var dogIds = dogRepository.findAllByEntityStatusAndName(EntityStatus.ACTIVE, name).stream().map(Dog::getId).collect(Collectors.toSet());

        var qMember = QMember.member;
        var memberQuery = qMember.entityStatus.eq(EntityStatus.ACTIVE).and(qMember.firstName.eq(name).or(qMember.lastName.eq(name)));
        var memberIds = StreamSupport.stream(memberRepository.findAll(memberQuery).spliterator(), false).map(Member::getId).collect(Collectors.toSet());

        var qDogHasHandler = QDogHasHandler.dogHasHandler;
        var dogHasHandlerQuery = qDogHasHandler.entityStatus.eq(EntityStatus.ACTIVE).and(qDogHasHandler.dogId.in(dogIds).or(qDogHasHandler.memberId.in(memberIds)));

        return StreamSupport.stream(dogHasHandlerRepository.findAll(dogHasHandlerQuery).spliterator(), false).collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public Set<DogHasHandler> getAllDogHasHandler() {
        Set<DogHasHandler> dogHasHandlers = dogHasHandlerRepository.findAllByEntityStatus(EntityStatus.ACTIVE);
        dogHasHandlers.forEach(this::setDogAndMemberIfTheyAreNull);
        return dogHasHandlers;
    }

    @Override
    public Set<DogHasHandler> getDogHasHandlersByIds(final Set<UUID> handlerIds) {
        Set<DogHasHandler> dogHasHandlers = dogHasHandlerRepository.findAllByEntityStatusAndIdIn(EntityStatus.ACTIVE, handlerIds);
        transactionTemplate.executeWithoutResult(ignore -> dogHasHandlers.forEach(this::setDogAndMemberIfTheyAreNull));
        return dogHasHandlers;

    }

    @Override
    public Set<UUID> getDogHasHandlerIdsByMemberId(final UUID memberId) {
        return getHandlersByMemberId(memberId).stream().map(DogHasHandler::getId).collect(Collectors.toSet());
    }

    @Override
    public Set<UUID> getDogHasHandlerIdsByDogId(final UUID dogId) {
        return getHandlersByDogId(dogId).stream().map(DogHasHandler::getId).collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public Set<String> getMembersEmailByIds(final Set<UUID> handlerIds) {
        return getDogHasHandlersByIds(handlerIds).stream()
                .map(dh -> dh.getMember().getEmail())
                .collect(Collectors.toSet());
    }

    private DogHasHandler setDogAndMemberIfTheyAreNull(final DogHasHandler dh) {
        if (null == dh.getDog() || null == dh.getMember()) {
            setDogAndMember(dh);
        }
        return dh;
    }

    private void setDogAndMember(final DogHasHandler dogHasHandler) {
        memberRepository.findByIdAndEntityStatus(dogHasHandler.getMemberId(), EntityStatus.ACTIVE).ifPresent(dogHasHandler::setMember);
        dogRepository.findDogByIdAndEntityStatus(dogHasHandler.getDogId(), EntityStatus.ACTIVE).ifPresent(dogHasHandler::setDog);
    }
}