package casp.web.backend.presentation.layer.dtos.event.participants;

import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CoTrainerMapper extends BaseParticipantMapper<CoTrainer, CoTrainerDto> {
    CoTrainerMapper CO_TRAINER_MAPPER = Mappers.getMapper(CoTrainerMapper.class);
}
