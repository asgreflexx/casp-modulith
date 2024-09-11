package casp.web.backend.data.access.layer.custom.repositories;

import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import jakarta.annotation.Nullable;

import java.util.Set;

public interface DogHasHandlerCustomRepository {
    Set<DogHasHandler> findAllByMemberNameOrDogName(@Nullable String name);
}
