package casp.web.backend.dog.data;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DogCustomRepository {
    Page<Dog> findAllByEuropeNetStateNotChecked(Pageable pageable);

    Page<Dog> findAllByValue(@Nullable String value, Pageable pageable);
}
