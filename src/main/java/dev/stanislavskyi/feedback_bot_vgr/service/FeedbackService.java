package dev.stanislavskyi.feedback_bot_vgr.service;

import dev.stanislavskyi.feedback_bot_vgr.dto.request.FeedbackRequest;
import dev.stanislavskyi.feedback_bot_vgr.dto.response.FeedbackAnalysisResponse;
import dev.stanislavskyi.feedback_bot_vgr.service.gemini_ai_api.service.FeedbackAnalysisService;
import dev.stanislavskyi.feedback_bot_vgr.service.google_docs.service.GoogleDocsService;
import dev.stanislavskyi.feedback_bot_vgr.service.trello_api.service.TrelloService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    @Value("${app.google-docs.document-id}")
    private String DOCUMENT_ID;

    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);

    private final GoogleDocsService googleDocsService;
    private final FeedbackStorageService feedbackStorageService;
    private final TrelloService trelloService;


    private final FeedbackAnalysisService feedbackAnalysisService;


    public FeedbackAnalysisResponse handleFeedback(FeedbackRequest request) {
        FeedbackAnalysisResponse analysis = feedbackAnalysisService.analyzeReview(request);

        feedbackStorageService.save(request, analysis);

        String textToSave = formatForDocs(analysis);

        createTrelloCardIfCritical(request, analysis, textToSave);

        googleDocsService.appendText(DOCUMENT_ID, textToSave);

        return analysis;
    }


    private String formatForDocs(FeedbackAnalysisResponse analysis) {
        StringBuilder sb = new StringBuilder();

        sb.append("          АНАЛІЗ ВІДГУКУ           \n");
        sb.append("══════════════════════════════════\n");
        sb.append("📌 Роль користувача: ").append(analysis.getRoleUser()).append("\n");
        sb.append("🏢 Відділення СТО: ").append(analysis.getAutoServiceBranch()).append("\n");
        sb.append("😐 Настрій: ").append(analysis.getSentiment()).append("\n");
        sb.append("⚠️ Критичність: ").append(analysis.getCriticality()).append("\n");
        sb.append("📝 Рекомендації:\n");

        for (String step : analysis.getResolution()) {
            sb.append("   - ").append(step).append("\n");
        }

        sb.append("📅 Дата створення: ").append(analysis.getCreatedAt()).append("\n");
        return sb.toString();
    }

    private void createTrelloCardIfCritical(FeedbackRequest request, FeedbackAnalysisResponse analysis, String textToSave){
        if (analysis.getCriticality() >= 4) {
            trelloService.createCard(request.getFeedbackText(), textToSave)
                    .subscribe(
                            cardId -> log.info("Trello card created: {}", cardId),
                            error -> log.error("Failed to create Trello card", error)
                    );
        }
    }
}
