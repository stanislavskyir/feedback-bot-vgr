package dev.stanislavskyi.feedback_bot_vgr.dto.response;

import dev.stanislavskyi.feedback_bot_vgr.model.RoleUser;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;

    private String telegramId;

    private RoleUser roleUser;

    private String autoServiceBranch;

    private LocalDateTime createdAt;

}
