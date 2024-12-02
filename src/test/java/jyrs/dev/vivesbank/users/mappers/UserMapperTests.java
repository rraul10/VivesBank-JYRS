package jyrs.dev.vivesbank.users.mappers;

import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import jyrs.dev.vivesbank.users.users.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTests {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void fromUserDto_ShouldMapToUserEntity() {
        // Arrange
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("testUser");
        userRequestDto.setPassword("password123");
        userRequestDto.setFotoPerfil("profile.png");
        userRequestDto.setIsDeleted(false);

        // Act
        User user = userMapper.fromUserDto(userRequestDto);

        // Assert
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("profile.png", user.getFotoPerfil());
        assertEquals(false, user.getIsDeleted());
    }

    @Test
    void toUserResponse_ShouldMapToUserResponseDto() {
        // Arrange
        User user = new User();
        user.setGuuid("puZjCDm_xCg");
        user.setUsername("testUser");
        user.setFotoPerfil("profile.png");
        user.setIsDeleted(false);

        // Act
        UserResponseDto userResponseDto = userMapper.toUserResponse(user);

        // Assert
        assertNotNull(userResponseDto);
        assertEquals("puZjCDm_xCg", userResponseDto.getGuuid());
        assertEquals("testUser", userResponseDto.getUsername());
        assertEquals("profile.png", userResponseDto.getFotoPerfil());
        assertEquals(false, userResponseDto.getIsDeleted());
    }

    @Test
    void toUser_ShouldUpdateUserEntity() {
        // Arrange
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("updatedUser");
        userRequestDto.setPassword("newPassword456");
        userRequestDto.setFotoPerfil("newProfile.png");
        userRequestDto.setIsDeleted(true);

        User existingUser = User.builder()
                .id(1L)
                .guuid("puZjCDm_xCg")
                .username("oldUser")
                .password("oldPassword")
                .fotoPerfil("oldProfile.png")
                .isDeleted(false)
                .build();

        // Act
        User updatedUser = userMapper.toUser(userRequestDto, existingUser);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(1L, updatedUser.getId());
        assertEquals("puZjCDm_xCg", updatedUser.getGuuid());
        assertEquals("updatedUser", updatedUser.getUsername());
        assertEquals("newPassword456", updatedUser.getPassword());
        assertEquals("newProfile.png", updatedUser.getFotoPerfil());
        assertEquals(true, updatedUser.getIsDeleted());
    }
}
