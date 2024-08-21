package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.Roles;
import casp.web.backend.data.access.layer.documents.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MemberRepository extends MongoRepository<Member, UUID>, QuerydslPredicateExecutor<Member> {

    Set<Member> findAllByFirstNameAndLastName(String firstName, String lastName);

    Page<Member> findAllByRoles(Roles roles, Pageable pageable);

    /**
     * @deprecated a new Query should be implemented
     */
    @Deprecated(forRemoval = true)
    @Query("SELECT m FROM Member m JOIN m.roles r WHERE r.name = :roles AND m.entityStatus = :entityStatus")
    Set<Member> findAllMembersByRoles(@Param("roles") Roles roles, @Param("entityStatus") EntityStatus entityStatus);

    Long countAllByEntityStatus(EntityStatus entityStatus);

    Set<Member> findAllByEntityStatus(EntityStatus entityStatus);

    /**
     * @deprecated a new Query should be implemented
     */
    @Deprecated(forRemoval = true)
    @Query("SELECT m " +
            "FROM Member m " +
            "WHERE (LOWER(m.firstName) LIKE LOWER(:name) " +
            "OR LOWER(m.lastName) LIKE LOWER(:name) " +
            "OR :name = '' OR :name IS NULL) " +
            "AND m.entityStatus = 'ACTIVE'" +
            "ORDER BY m.firstName ASC, m.lastName ASC")
    Set<Member> searchMembersByFirstNameOrLastName(@Param("name") String name);

    Set<Member> findAllByFirstNameOrLastName(String firstName, String lastName);

    Optional<Member> findMemberByEmail(String email);

    Page<Member> findAllByEntityStatusOrderByLastName(
            EntityStatus entityStatus,
            Pageable pageable
    );

    Set<Member> findAllByIdInAndEntityStatus(Set<UUID> membersId, EntityStatus entityStatus);
}
