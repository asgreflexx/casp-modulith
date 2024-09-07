package casp.web.backend.presentation.layer.member;

import casp.web.backend.data.access.layer.documents.member.Member;
import casp.web.backend.presentation.layer.configuration.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper extends BaseMapper<Member, MemberDto> {
}
