package casp.web.backend.member.presentation;


import casp.web.backend.common.base.BaseViewMapper;
import casp.web.backend.member.MemberDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface MemberWriteMapper extends BaseViewMapper<MemberDto, MemberWrite> {
    MemberWriteMapper WRITE_MAPPER = Mappers.getMapper(MemberWriteMapper.class);
}
