package com.sparta.project.Repository;

import com.sparta.project.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
//    List<Comment> findAllByPostIdOrderByCreatedAt(Long id);
//    void deleteAllByPostId(Long id);
//    Optional<Comment> findByIdAndUser_Id(Long commentId, Long userId);
}
