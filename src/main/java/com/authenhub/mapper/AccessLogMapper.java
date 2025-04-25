package com.authenhub.mapper;

import com.authenhub.dto.AccessLogDTO;
import com.authenhub.entity.mongo.AccessLog;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccessLogMapper {

    AccessLogDTO toDto(com.authenhub.entity.mongo.AccessLog entity);

    AccessLog toMongoEntity(AccessLogDTO dto);

    List<AccessLogDTO> toDtoListFromMongo(List<com.authenhub.entity.mongo.AccessLog> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "extraData", ignore = true)
//    @Mapping(target = "sposQrSelfOrder", ignore = true)
    void updateAccessLogFromRequest(AccessLogDTO accessLogDTO, @MappingTarget AccessLog accessLog);
}
