package dev.stanislavskyi.feedback_bot_vgr.service.gemini_ai_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.stanislavskyi.feedback_bot_vgr.dto.request.FeedbackRequest;
import dev.stanislavskyi.feedback_bot_vgr.dto.response.FeedbackAnalysisResponse;
import dev.stanislavskyi.feedback_bot_vgr.service.FeedbackStorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class FeedbackAnalysisService {

    private final GeminiService geminiService;
    private static final Logger log = LoggerFactory.getLogger(FeedbackAnalysisService.class);

    public FeedbackAnalysisResponse analyzeReview(FeedbackRequest review) {
        String prompt = createPrompt(review);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("RESPONSE FROM AI: {} ", aiResponse);

        FeedbackAnalysisResponse analysis = processAiResponse(review, aiResponse);
        return analysis;
    }

    private String createPrompt(FeedbackRequest feedbackRequest) {
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
}
