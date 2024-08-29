package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.member.Member;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberCustomRepository {
    List<Member> findAllByFirstNameAndLastName(@Nullable String firstName, @Nullable String lastName);

    Page<Member> findAllByValue(@Nullable String value, Pageable pageable);
}
