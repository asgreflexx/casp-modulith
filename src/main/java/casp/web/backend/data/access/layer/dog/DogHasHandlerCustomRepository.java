package casp.web.backend.data.access.layer.dog;

import jakarta.annotation.Nullable;

import java.util.Set;

public interface DogHasHandlerCustomRepository {
    Set<DogHasHandler> findAllByMemberNameOrDogName(@Nullable String name);
}
