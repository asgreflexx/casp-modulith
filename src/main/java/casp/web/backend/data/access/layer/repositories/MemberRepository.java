package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.custom.repositories.MemberCustomRepository;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MemberRepository extends MongoRepository<Member, UUID>, QuerydslPredicateExecutor<Member>, MemberCustomRepository {

    Page<Member> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);

    Optional<Member> findMemberByEmail(String email);

    Set<Member> findAllByIdInAndEntityStatus(Set<UUID> membersId, EntityStatus entityStatus);

    Optional<Member> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Optional<Member> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);
}
