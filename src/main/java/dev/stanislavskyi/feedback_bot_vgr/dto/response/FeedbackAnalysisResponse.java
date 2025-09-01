package dev.stanislavskyi.feedback_bot_vgr.dto.response;

import dev.stanislavskyi.feedback_bot_vgr.model.RoleUser;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FeedbackAnalysisResponse {

    private RoleUser roleUser;
    private String autoServiceBranch;
    private String sentiment;
    private int criticality;
    private List<String> resolution;
    private LocalDateTime createdAt;
}