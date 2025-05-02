package com.authenhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "chat_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_session_seq")
    @SequenceGenerator(name = "chat_session_seq", sequenceName = "chat_session_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Timestamp createdAt;

    private Timestamp lastMessageAt;

    private String title;

    private Integer messageCount;

    private Integer tokenCount;

    private String model; // gpt-3.5, gpt-4, claude, llama
}
