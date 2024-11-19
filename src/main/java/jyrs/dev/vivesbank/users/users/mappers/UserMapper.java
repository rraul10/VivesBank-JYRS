package jyrs.dev.vivesbank.users.users.mappers;

import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
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

}
