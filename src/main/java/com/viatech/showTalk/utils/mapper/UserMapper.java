package com.viatech.showTalk.utils.mapper;

import com.viatech.showTalk.DTO.RegisterResponse;
import com.viatech.showTalk.entites.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {



        RegisterResponse userToResponse(User user);


}
