package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.documents.event.participants.ExamParticipant;
import casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerDto;

public class ExamParticipantDto extends ExamParticipant {
    private DogHasHandlerDto dogHasHandler;

    public DogHasHandlerDto getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandlerDto dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }
}
