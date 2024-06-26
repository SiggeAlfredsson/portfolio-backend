package com.siggebig.repositorys;

import com.siggebig.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(Long userId);

    Page<Post> findByIsPrivateFalse(Pageable pageable);

    Page<Post> findByIsPrivateFalseOrderByCreatedAtDesc(Pageable pageable);
//    List<Post> findByIsPrivateFalse();

//    List<Post> findAll(Pageable pageable);


}
