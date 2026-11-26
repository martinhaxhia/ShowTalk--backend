package com.viatech.showTalk.utils.mapper;

import com.viatech.showTalk.DTO.AppUserRegisterRequest;
import com.viatech.showTalk.DTO.AppUserResponse;
import com.viatech.showTalk.entites.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

        AppUser registerRequestToUser(AppUserRegisterRequest request);   //why you need this?

        AppUserResponse userToResponse(AppUser user);


}
