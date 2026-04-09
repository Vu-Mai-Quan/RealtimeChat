package com.example.realtimechat.mapper;

import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.db1.model.NguoiDung.NguoiDungDangKi;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public abstract class NguoiDungMapper {

    @Mappings({
            @Mapping(target = "displayName", source = "nguoiDungDangKi.displayName"),
            @Mapping(target = "password",ignore = true),
    })
    public abstract NguoiDung dangKiMapper(NguoiDungDangKi nguoiDungDangKi);



    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract NguoiDung partialUpdate(NguoiDungDangKi nguoiDungDangKi,
                                            @MappingTarget
                                            NguoiDung nguoiDung);
}