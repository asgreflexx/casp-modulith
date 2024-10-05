package casp.web.backend.data.access.layer.dog;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DogCustomRepository {
    List<Dog> findAllByChipNumberOrDogNameOrOwnerName(@Nullable String chipNumber, @Nullable String dogName, @Nullable String ownerName);

    Page<Dog> findAllByEuropeNetStateNotChecked(final Pageable pageable);
}
