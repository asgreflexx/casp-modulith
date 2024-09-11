package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.documents.event.participants.Space;
import casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerDto;

public class SpaceDto extends Space {
    private DogHasHandlerDto dogHasHandler;

    public DogHasHandlerDto getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandlerDto dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }
}
