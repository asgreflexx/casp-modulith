package casp.web.backend.business.logic.layer.event.participants;

import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import casp.web.backend.data.access.layer.documents.event.participants.BaseParticipant;

public record ParticipantDogHasHandler(BaseParticipant participant, DogHasHandler dogHasHandler) {
}
