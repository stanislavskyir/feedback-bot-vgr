package dev.stanislavskyi.feedback_bot_vgr.mapper;

import dev.stanislavskyi.feedback_bot_vgr.dto.request.RegisterRequest;
import dev.stanislavskyi.feedback_bot_vgr.dto.response.UserResponse;
import dev.stanislavskyi.feedback_bot_vgr.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    User fromRegisterRequest(RegisterRequest request);

}
