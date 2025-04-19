package org.core.dnd_ai.security.users;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class UserMapper {
    abstract User toEntity(PostUserDTO dto);

    abstract GetUserDTO toDTO(User user);
}
