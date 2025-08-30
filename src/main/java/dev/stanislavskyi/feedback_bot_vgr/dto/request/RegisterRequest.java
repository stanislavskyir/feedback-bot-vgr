package dev.stanislavskyi.feedback_bot_vgr.dto.request;

import dev.stanislavskyi.feedback_bot_vgr.model.RoleUser;
import lombok.Data;

@Data
public class RegisterRequest {

    private String telegramId;
    private RoleUser roleUser;
    private String autoServiceBranch;
}
