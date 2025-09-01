package dev.stanislavskyi.feedback_bot_vgr.dto.response;

import dev.stanislavskyi.feedback_bot_vgr.model.RoleUser;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReviewAnalysisResponse {

    private RoleUser roleUser;               // Роль пользователя, который оставил отзыв
    private String autoServiceBranch;        // Название филиала авто-сервиса
    private String sentiment;                // negative / neutral / positive
    private int criticality;                 // уровень критичности 1-5
    private List<String> resolution;         // шаги/рекомендации по решению вопроса
    private LocalDateTime createdAt;         // дата и время создания анализа
}