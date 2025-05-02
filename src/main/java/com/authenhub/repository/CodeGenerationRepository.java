package com.authenhub.repository;

import com.authenhub.entity.CodeGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeGenerationRepository extends JpaRepository<CodeGeneration, Long> {
    
    List<CodeGeneration> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<CodeGeneration> findByUserIdAndCodeTypeOrderByCreatedAtDesc(String userId, String codeType);
    
    List<CodeGeneration> findByUserIdAndFavoriteIsTrueOrderByCreatedAtDesc(String userId);
}
