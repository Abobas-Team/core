package org.core.dnd_ai.security.users;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {
    public abstract User toEntity(PostUserDTO dto);

    public abstract GetUserDTO toDTO(User user);
}
