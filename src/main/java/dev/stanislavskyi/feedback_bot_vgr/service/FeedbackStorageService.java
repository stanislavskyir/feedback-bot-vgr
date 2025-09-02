package dev.stanislavskyi.feedback_bot_vgr.service;

import dev.stanislavskyi.feedback_bot_vgr.dto.request.FeedbackRequest;
import dev.stanislavskyi.feedback_bot_vgr.dto.response.FeedbackAnalysisResponse;
import dev.stanislavskyi.feedback_bot_vgr.mapper.FeedbackMapper;
import dev.stanislavskyi.feedback_bot_vgr.model.Feedback;
import dev.stanislavskyi.feedback_bot_vgr.model.RoleUser;
import dev.stanislavskyi.feedback_bot_vgr.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//Отдельный сервис для сохранения и получения отзывов

@Service
@RequiredArgsConstructor
public class FeedbackStorageService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private static final Logger log = LoggerFactory.getLogger(FeedbackStorageService.class);


    public Feedback save(FeedbackRequest review, FeedbackAnalysisResponse analysis) {
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

    public List<Feedback> getFilteredFeedbacks(String branch, RoleUser roleUser, Integer criticality) {
        return feedbackRepository.findAll().stream()
                .filter(f -> branch == null || branch.isEmpty() || f.getAutoServiceBranch().equals(branch))
                .filter(f -> roleUser == null || f.getRoleUser() == roleUser)
                .filter(f -> criticality == null || f.getCriticality().equals(criticality))
                .collect(Collectors.toList());
    }
}
