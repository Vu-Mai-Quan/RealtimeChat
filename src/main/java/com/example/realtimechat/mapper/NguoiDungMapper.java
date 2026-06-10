package com.example.realtimechat.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.db1.model.NguoiDung.NguoiDungDangKi;

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