package com.authenhub.repository;

import com.authenhub.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findBySessionIdOrderByTimestamp(String sessionId);
    
    Optional<ChatMessage> findByMessageId(String messageId);
}
