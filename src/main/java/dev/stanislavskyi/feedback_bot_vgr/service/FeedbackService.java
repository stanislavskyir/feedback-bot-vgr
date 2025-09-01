package dev.stanislavskyi.feedback_bot_vgr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.stanislavskyi.feedback_bot_vgr.dto.request.FeedbackRequest;
import dev.stanislavskyi.feedback_bot_vgr.dto.response.FeedbackAnalysisResponse;
import dev.stanislavskyi.feedback_bot_vgr.gemini_ai_api.service.GeminiService;
import dev.stanislavskyi.feedback_bot_vgr.google_docs.service.GoogleDocsService;
import dev.stanislavskyi.feedback_bot_vgr.mapper.FeedbackMapper;
import dev.stanislavskyi.feedback_bot_vgr.model.Feedback;
import dev.stanislavskyi.feedback_bot_vgr.repository.FeedbackRepository;
import dev.stanislavskyi.feedback_bot_vgr.trello_api.service.TrelloService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);
    private final GeminiService geminiService;
    private final GoogleDocsService googleDocsService;
    private final FeedbackRepository feedbackRepository;

    private final FeedbackMapper feedbackMapper;

    private final TrelloService trelloService;

    @Value("${app.google-docs.document-id}")
    private String DOCUMENT_ID;

    public FeedbackAnalysisResponse analyzeReview(FeedbackRequest review) {
        String prompt = createPromptForReview(review);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("RESPONSE FROM AI: {} ", aiResponse);

        FeedbackAnalysisResponse analysis = processAiResponse(review, aiResponse);

        try {
            Feedback feedback = saveFeedbackToDb(review, analysis);

            String textToSave = formatForDocs(analysis);

            if(analysis.getCriticality() >= 4){
                trelloService.createCard(review.getFeedbackText(), textToSave)//analysis.toString())
                        .subscribe(
                                cardId -> log.info("Trello card created: {}", cardId),
                                error -> log.error("Failed to create Trello card", error)
                        );
            }


            googleDocsService.appendText(DOCUMENT_ID, textToSave);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return analysis;
    }

    private Feedback saveFeedbackToDb(FeedbackRequest review, FeedbackAnalysisResponse analysis) {
        try {
            Feedback feedback = feedbackMapper.toEntity(analysis);
            feedback.setFeedbackText(review.getFeedbackText());

            Feedback savedFeedback = feedbackRepository.save(feedback);

            return savedFeedback;

        } catch (Exception e) {
            log.error("Failed to save feedback to DB", e);
            throw new RuntimeException("Failed to save feedback", e);
        }
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

    private FeedbackAnalysisResponse processAiResponse(FeedbackRequest feedbackRequest, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");


            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();

            JsonNode parsedNode = mapper.readTree(jsonContent);

            JsonNode sentimentNode = parsedNode.path("sentiment");
            JsonNode criticalityNode = parsedNode.path("criticality");
            JsonNode resolutionNode = parsedNode.path("resolution");

            return FeedbackAnalysisResponse.builder()
                    .roleUser(feedbackRequest.getRoleUser())
                    .autoServiceBranch(feedbackRequest.getAutoServiceBranch())
                    .sentiment(sentimentNode.asText("unknown"))
                    .criticality(criticalityNode.asInt(1))
                    .resolution(resolutionNode.isMissingNode() ?
                            Collections.singletonList("No suggestions provided") :
                            mapper.convertValue(resolutionNode, java.util.List.class))
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return createDefaultAnalysis(feedbackRequest);
        }
    }

    private FeedbackAnalysisResponse createDefaultAnalysis(FeedbackRequest feedbackRequest) {
        return FeedbackAnalysisResponse.builder()
                .roleUser(feedbackRequest.getRoleUser())
                .autoServiceBranch(feedbackRequest.getAutoServiceBranch())
                .sentiment("neutral")
                .criticality(3)
                .resolution(Collections.singletonList("Review requires manual processing"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String createPromptForReview(FeedbackRequest feedbackRequest) {
        return String.format("""
                Analyze the following user feedback and provide the output in the EXACT JSON format below:
                {
                  "sentiment": "negative/neutral/positive",
                  "criticality": 1-5,
                  "resolution": [
                    "Step 1 suggestion",
                    "Step 2 suggestion"
                  ]
                }
                
                Feedback Text: "%s"
                
                Instructions:
                1. Determine whether the feedback is negative, neutral, or positive.
                2. Assign a criticality level from 1 (least critical) to 5 (most critical).
                3. Suggest ways to resolve the issue mentioned in the feedback, 
                   explicitly from the perspective of the **management / company / employer**.
                4. Respond ONLY in the EXACT JSON format above.
                5. All text (sentiment description and resolution steps) must be in Ukrainian.
                
                Make sure the resolution steps are actions that the employer or management should take, not the employee.
                """, feedbackRequest.getFeedbackText());
    }
}
