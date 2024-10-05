package casp.web.backend.data.access.layer.event.types;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseEventCustomRepository {
    Page<BaseEvent> findAllByYearAndEventType(int year, final String eventType, Pageable pageable);
}
