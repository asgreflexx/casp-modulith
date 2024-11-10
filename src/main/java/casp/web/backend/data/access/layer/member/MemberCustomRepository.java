package casp.web.backend.data.access.layer.member;

import casp.web.backend.common.enums.EntityStatus;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MemberCustomRepository {
    Page<Member> findAllByFirstNameAndLastName(@Nullable String firstName, @Nullable String lastName, Pageable pageable);

    Page<Member> findAllByValue(@Nullable String value, Pageable pageable);

    Member findByIdAndEntityStatusCustom(UUID id, EntityStatus entityStatus);
}
