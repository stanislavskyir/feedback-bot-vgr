package dev.stanislavskyi.feedback_bot_vgr.service;

import dev.stanislavskyi.feedback_bot_vgr.dto.request.RegisterRequest;
import dev.stanislavskyi.feedback_bot_vgr.dto.response.UserResponse;
import dev.stanislavskyi.feedback_bot_vgr.exception.UserAlreadyExistsException;
import dev.stanislavskyi.feedback_bot_vgr.model.User;
import dev.stanislavskyi.feedback_bot_vgr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);


    @Transactional
    public UserResponse createUser(RegisterRequest request){

        userRepository.findByTelegramId(request.getTelegramId()).ifPresent(
                user -> {
                    throw new UserAlreadyExistsException("User with this telegramId already exists");
                }
        );

        User user = new User();
        user.setRoleUser(request.getRoleUser());
        user.setAutoServiceBranch(request.getAutoServiceBranch());
        user.setTelegramId(request.getTelegramId());

        UserResponse userResponse = new UserResponse();
        userResponse.setRoleUser(user.getRoleUser());
        user.setAutoServiceBranch(request.getAutoServiceBranch());
        user.setTelegramId(request.getTelegramId());

        log.info("Created user: id={}, telegramId={}", userResponse.getId(), userResponse.getTelegramId());

        return userResponse;
    }

}
