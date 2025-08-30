package dev.stanislavskyi.feedback_bot_vgr.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String telegramId;

    @Enumerated(EnumType.STRING)
    private RoleUser roleUser;

    @Column(nullable = false)
    private String autoServiceBranch;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
