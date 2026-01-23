package com.example.fleamarketsystem.repository;

import com.example.fleamarketsystem.entity.Star;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StarRepository extends JpaRepository<Star, Long> {
    
    List<Star> findByTargetUserId(Long targetUserId);
    
    Optional<Star> findByUserIdAndTargetUserId(Long userId, Long targetUserId);
    
    @Query("SELECT AVG(s.rating) FROM Star s WHERE s.targetUser.id = :targetUserId")
    Double getAverageRatingForUser(@Param("targetUserId") Long targetUserId);
    
    @Query("SELECT COUNT(s) FROM Star s WHERE s.targetUser.id = :targetUserId")
    Long getStarCountForUser(@Param("targetUserId") Long targetUserId);
}
