package dev.stanislavskyi.feedback_bot_vgr.dto.request;

import dev.stanislavskyi.feedback_bot_vgr.model.RoleUser;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class FeedbackRequest {

    @NotNull(message = "Role is required")
    private RoleUser roleUser;

    @NotBlank(message = "Auto service branch is required")
    private String autoServiceBranch;

    @NotBlank(message = "Feedback text is required")
    private String feedbackText;
}
