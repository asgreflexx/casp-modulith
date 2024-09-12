package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.event.participants.BaseParticipant;

public record ParticipantDogHasHandler(BaseParticipant participant, DogHasHandler dogHasHandler) {
}
