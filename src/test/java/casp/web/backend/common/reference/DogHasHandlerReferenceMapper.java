package casp.web.backend.common.reference;

import casp.web.backend.dog.data.Dog;
import casp.web.backend.member.data.Member;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DogHasHandlerReferenceMapper {
    DogHasHandlerReferenceMapper DOG_HAS_HANDLER_REFERENCE_MAPPER = Mappers.getMapper(DogHasHandlerReferenceMapper.class);

    DogReference toDogReference(Dog dog);

    MemberReference toMemberReference(Member member);
}
