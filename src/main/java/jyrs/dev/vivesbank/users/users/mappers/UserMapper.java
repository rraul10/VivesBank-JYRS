package jyrs.dev.vivesbank.users.users.mappers;

import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import org.springframework.stereotype.Component;

/**
 * Mapper de usuario a sus respectivos dtos para las responses y requests.
 */

@Component
public class UserMapper {
    /**
     * Mapea un UserRequest a un user
     * @param userRequestDto
     * @return User
     */
     public User fromUserDto(UserRequestDto userRequestDto) {
         var user = new User();
         user.setUsername(userRequestDto.getUsername());
         user.setPassword(userRequestDto.getPassword());
         user.setFotoPerfil(userRequestDto.getFotoPerfil());
         user.setIsDeleted(userRequestDto.getIsDeleted());
         return user;
     }

    /**
     * Mapea un user a un UserResponse
     * @param user
     * @return UserResponseDto
     */
     public UserResponseDto toUserResponse(User user){
         return new UserResponseDto(
                 user.getGuuid(),
                 user.getUsername(),
                 user.getFotoPerfil(),
                 user.getIsDeleted()
         );
     }

    /**
     * Mapea un userRequest a un usuario
     * @param userRequest
     * @param user
     * @return user
     */
     public User toUser(UserRequestDto userRequest, User user){
         return User.builder()
                 .id(user.getId())
                 .guuid(user.getGuuid())
                 .username(userRequest.getUsername())
                 .password(userRequest.getPassword())
                 .fotoPerfil(userRequest.getFotoPerfil())
                 .isDeleted(userRequest.getIsDeleted())
                 .build();
     }



}
