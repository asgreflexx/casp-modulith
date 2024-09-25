package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerDto;

public class ExamParticipantDto extends BaseParticipantDto {
    private DogHasHandlerDto dogHasHandler;

    public ExamParticipantDto() {
        super(ExamParticipant.PARTICIPANT_TYPE);
    }

    public DogHasHandlerDto getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandlerDto dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }
}
