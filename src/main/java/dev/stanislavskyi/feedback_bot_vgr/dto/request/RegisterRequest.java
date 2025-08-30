package dev.stanislavskyi.feedback_bot_vgr.dto.request;

import dev.stanislavskyi.feedback_bot_vgr.model.RoleUser;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Telegram ID is required")
    private String telegramId;

    private RoleUser roleUser;

    private String autoServiceBranch;
}
