package org.core.dnd_ai.security.users;

import org.core.dnd_ai.security.oauth2.GetUserInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {
    public abstract LocalUser toLocalUser(PostUserDTO dto);

    @Mapping(target = "userInfo", expression = "java(processUserInfo(user))")
    public abstract GetUserDTO toDTO(User user);

    protected GetUserInfoDTO processUserInfo(User user) {
        if (user instanceof OAuthUser oAuthUser) {
            var userInfo = oAuthUser.getUserInfo();
            return new GetUserInfoDTO(
                    userInfo.getProvider(),
                    userInfo.getId(),
                    userInfo.getName(),
                    userInfo.getEmail(),
                    userInfo.getImage());
        }
        return null;
    }
}
