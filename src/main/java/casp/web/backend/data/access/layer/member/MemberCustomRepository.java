package casp.web.backend.data.access.layer.member;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MemberCustomRepository {
    List<Member> findAllByFirstNameAndLastName(@Nullable String firstName, @Nullable String lastName);

    Page<Member> findAllByValue(@Nullable String value, Pageable pageable);

    Member findByIdAndEntityStatusCustom(UUID id, EntityStatus entityStatus);
}
