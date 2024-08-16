package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.enumerations.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IMemberRepository extends MongoRepository<Member, UUID> {

    Collection<Member> findAllByFirstNameAndLastName(String firstName, String lastName);

    Page<Member> findAllByRoles(Roles roles, Pageable pageable);

    @Query("SELECT m FROM Member m JOIN m.roles r WHERE r.name = :roles AND m.entityStatus = :entityStatus")
    List<Member> findAllMembersByRoles(@Param("roles") Roles roles, @Param("entityStatus") EntityStatus entityStatus);

    Long countAllByEntityStatus(EntityStatus entityStatus);

    List<Member> findAllByEntityStatus(EntityStatus entityStatus);

    @Query("SELECT m " +
            "FROM Member m " +
            "WHERE (LOWER(m.firstName) LIKE LOWER(:name) " +
            "OR LOWER(m.lastName) LIKE LOWER(:name) " +
            "OR :name = '' OR :name IS NULL) " +
            "AND m.entityStatus = 'ACTIVE'" +
            "ORDER BY m.firstName ASC, m.lastName ASC")
    List<Member> searchMembersByFirstNameOrLastName(@Param("name") String name);

    List<Member> findAllByFirstNameOrLastName(String firstName, String lastName);

    Optional<Member> findMemberByEmail(String email);

    Page<Member> findAllByEntityStatusOrderByLastName(
            EntityStatus entityStatus,
            Pageable pageable
    );

    Set<Member> findAllByIdInAndEntityStatus(Set<UUID> membersId, EntityStatus entityStatus);
}
