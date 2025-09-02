package dev.stanislavskyi.feedback_bot_vgr.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedbacks")
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RoleUser roleUser;

    @Column(nullable = false)
    private String autoServiceBranch;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String feedbackText;

    @Enumerated(EnumType.STRING)
    private SentimentType sentiment;

    @Column
    private Integer criticality;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
