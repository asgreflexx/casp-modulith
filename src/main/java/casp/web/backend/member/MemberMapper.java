package casp.web.backend.member;

import casp.web.backend.common.base.BaseDtoMapper;
import casp.web.backend.member.data.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberMapper extends BaseDtoMapper<Member, MemberDto> {
    MemberMapper MEMBER_MAPPER = Mappers.getMapper(MemberMapper.class);

    @Mapping(target = "entityStatus", ignore = true)
    @Override
    Member toSource(MemberDto source);
}
