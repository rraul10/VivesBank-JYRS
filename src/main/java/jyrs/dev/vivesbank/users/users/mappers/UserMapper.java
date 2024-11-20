package jyrs.dev.vivesbank.users.users.mappers;

import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
     public User fromUserDto(UserRequestDto userRequestDto) {
         var user = new User();
         user.setUsername(userRequestDto.username());
         user.setPassword(userRequestDto.password());
         user.setFotoPerfil(userRequestDto.fotoPerfil());
         user.setIsDeleted(userRequestDto.isDeleted());
         return user;
     }
     public UserResponseDto toUserResponse(User user){
         return new UserResponseDto(
                 user.getUsername(),
                 user.getFotoPerfil(),
                 user.getIsDeleted()
         );
     }

     public User toUser(UserRequestDto userRequest, User user){
         return User.builder()
                 .id(user.getId())
                 .username(userRequest.username())
                 .password(userRequest.password())
                 .fotoPerfil(userRequest.fotoPerfil())
                 .isDeleted(userRequest.isDeleted())
                 .build();
     }



}
