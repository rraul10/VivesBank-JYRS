package jyrs.dev.vivesbank.users.users.mappers;

import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
     public User fromUserDto(UserRequestDto userRequestDto) {
         var user = new User();
         user.setUsername(userRequestDto.getUsername());
         user.setPassword(userRequestDto.getPassword());
         user.setFotoPerfil(userRequestDto.getFotoPerfil());
         user.setIsDeleted(userRequestDto.getIsDeleted());
         return user;
     }
     public UserResponseDto toUserResponse(User user){
         return new UserResponseDto(
                 user.getGuuid(),
                 user.getUsername(),
                 user.getFotoPerfil(),
                 user.getIsDeleted()
         );
     }

     public User toUser(UserRequestDto userRequest, User user){
         return User.builder()
                 .id(user.getId())
                 .username(userRequest.getUsername())
                 .password(userRequest.getPassword())
                 .fotoPerfil(userRequest.getFotoPerfil())
                 .isDeleted(userRequest.getIsDeleted())
                 .build();
     }



}
