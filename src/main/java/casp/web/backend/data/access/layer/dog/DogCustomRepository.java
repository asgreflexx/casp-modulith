package casp.web.backend.data.access.layer.dog;

import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Set;

public interface DogCustomRepository {
    List<Dog> findAllByChipNumberOrDogNameOrOwnerName(@Nullable String chipNumber, @Nullable String dogName, @Nullable String ownerName);

    Set<Dog> findAllByEuropeNetStateNotChecked();
}
