package casp.web.backend.presentation.layer.dtos.member;

import casp.web.backend.data.access.layer.documents.member.Member;
import casp.web.backend.presentation.layer.dtos.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberMapper extends BaseMapper<Member, MemberDto> {
    MemberMapper MEMBER_MAPPER = Mappers.getMapper(MemberMapper.class);
}