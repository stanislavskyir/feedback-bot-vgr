package dev.stanislavskyi.feedback_bot_vgr.repository;

import dev.stanislavskyi.feedback_bot_vgr.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
}
