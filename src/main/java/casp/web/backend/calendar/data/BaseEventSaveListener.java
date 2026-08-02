package casp.web.backend.calendar.data;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
class BaseEventSaveListener extends AbstractMongoEventListener<BaseEvent<?>> {
    @Override
    public void onBeforeConvert(BeforeConvertEvent<BaseEvent<?>> event) {
        event.getSource().updateBounds();
    }
}
