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
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_message_seq")
    @SequenceGenerator(name = "chat_message_seq", sequenceName = "chat_message_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String messageId;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String sender; // user or bot

    @Column(nullable = false)
    private Timestamp timestamp;

    private Integer tokenCount;

    private String model; // gpt-3.5, gpt-4, claude, llama
}
