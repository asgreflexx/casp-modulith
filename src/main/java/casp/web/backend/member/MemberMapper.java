package casp.web.backend.member;

import casp.web.backend.common.base.BaseDtoMapper;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.member.data.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface MemberMapper extends BaseDtoMapper<Member, MemberDto> {
    MemberMapper MEMBER_MAPPER = Mappers.getMapper(MemberMapper.class);

    @Mapping(target = "entityStatus", ignore = true)
    @Override
    Member toSource(MemberDto source);

    @Mapping(target = "dogId", source = "dogHasHandler.dog.id")
    @Mapping(target = "dogName", source = "dogHasHandler.dog.name")
    DogHasHandlerDto toDogHasHandlerDto(DogHasHandlerReference dogHasHandler);

    Set<DogHasHandlerDto> toDogHasHandlerDtoSet(Set<DogHasHandlerReference> dogHasHandlerSet);
}
