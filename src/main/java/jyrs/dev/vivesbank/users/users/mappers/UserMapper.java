package jyrs.dev.vivesbank.users.users.mappers;

import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
     public User fromUserDto(UserDto userDto) {
         var user = new User();
         user.setUsername(userDto.username());
         user.setPassword(userDto.password());
         user.setFotoPerfil(userDto.fotoPerfil());
         user.setIsDeleted(userDto.isDeleted());
         return user;
     }

}
