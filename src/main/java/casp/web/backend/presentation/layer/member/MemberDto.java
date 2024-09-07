package casp.web.backend.presentation.layer.member;

import casp.web.backend.data.access.layer.documents.member.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class MemberDto extends Member {
}
