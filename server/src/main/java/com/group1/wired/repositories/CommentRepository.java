package com.group1.wired.repositories;

import com.group1.wired.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
	List<Comment> findByPost_PostIDOrderByTimestampAsc(Long postId);
}
