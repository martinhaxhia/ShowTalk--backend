package com.viatech.showTalk.utils.mapper;

import com.viatech.showTalk.DTO.AppUserRegisterRequest;
import com.viatech.showTalk.DTO.AppUserResponse;
import com.viatech.showTalk.entites.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

        AppUser registerRequestToUser(AppUserRegisterRequest request);

        AppUserResponse userToResponse(AppUser user);


}
