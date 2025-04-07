package casp.web.backend.dog.data;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DogCustomRepository {
    Page<Dog> findAllByNameOrOwnerName(@Nullable String dogName, @Nullable String ownerName, Pageable pageable);

    Page<Dog> findAllByEuropeNetStateNotChecked(Pageable pageable);

    Page<Dog> findAllByValue(@Nullable String value, Pageable pageable);

    Page<Dog> findAllByNotMemberId(UUID memberId, @Nullable String dogName, Pageable pageable);
}
