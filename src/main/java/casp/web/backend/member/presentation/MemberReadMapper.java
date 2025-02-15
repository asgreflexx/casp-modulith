package casp.web.backend.member.presentation;

import casp.web.backend.common.base.BaseViewMapper;
import casp.web.backend.member.MemberDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface MemberReadMapper extends BaseViewMapper<MemberDto, MemberRead> {
    MemberReadMapper READ_MAPPER = Mappers.getMapper(MemberReadMapper.class);
}
