package casp.web.backend.business.logic.layer.dog;

import casp.web.backend.data.access.layer.dog.Dog;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EuropeNetTasks {
    Page<Dog> registerDogsManually(@Nullable Pageable pageRequest);
}
