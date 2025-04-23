package com.authenhub.mapper;

import com.authenhub.dto.AccessLogDTO;
import com.authenhub.entity.AccessLog;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccessLogMapper {

    AccessLogDTO toDto(AccessLog entity);

    AccessLogDTO toDto(com.authenhub.entity.mongo.AccessLog entity);

    AccessLog toEntity(AccessLogDTO dto);

    com.authenhub.entity.mongo.AccessLog toMongoEntity(AccessLogDTO dto);

    @Mapping(target = "id", ignore = true)
    AccessLog mongoToJpa(com.authenhub.entity.mongo.AccessLog mongoEntity);

    //    @Mapping(target = "name", source = "terminalName")
    com.authenhub.entity.mongo.AccessLog jpaToMongo(AccessLog entity);

    List<AccessLogDTO> toDtoList(List<AccessLog> entities);

    List<AccessLogDTO> toDtoListFromMongo(List<com.authenhub.entity.mongo.AccessLog> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "extraData", ignore = true)
//    @Mapping(target = "sposQrSelfOrder", ignore = true)
    void updateAccessLogFromRequest(AccessLogDTO accessLogDTO, @MappingTarget AccessLog accessLog);
}
