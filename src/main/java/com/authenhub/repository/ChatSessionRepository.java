package com.authenhub.repository;

import com.authenhub.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    
    List<ChatSession> findByUserIdOrderByLastMessageAtDesc(String userId);
    
    Optional<ChatSession> findBySessionId(String sessionId);
}
