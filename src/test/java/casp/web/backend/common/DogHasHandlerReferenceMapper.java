package casp.web.backend.common;

import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.data.access.layer.dog.Dog;
import casp.web.backend.member.data.Member;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DogHasHandlerReferenceMapper {
    DogHasHandlerReferenceMapper DOG_HAS_HANDLER_REFERENCE_MAPPER = Mappers.getMapper(DogHasHandlerReferenceMapper.class);

    DogReference toDogReference(Dog dog);

    MemberReference toMemberReference(Member member);
}
