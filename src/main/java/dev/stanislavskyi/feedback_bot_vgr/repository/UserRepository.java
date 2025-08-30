package dev.stanislavskyi.feedback_bot_vgr.repository;

import dev.stanislavskyi.feedback_bot_vgr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByTelegramId(String telegramId);
}
