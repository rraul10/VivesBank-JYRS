package jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper;

import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto.UserNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationMapper {
    public UserNotificationResponse toUserNotificationResponse(User user){
        return new UserNotificationResponse(
                user.getGuuid(),
                user.getUsername()
        );
    }
}
