package com.example.scmodifyarticleinfoservice.repository;

import com.example.scmodifyarticleinfoservice.domain.Author;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface AuthorRepository extends CrudRepository<Author, Long> {
    @Transactional
    @Modifying
    @Query(value = "delete from AUTHOR where ARTICLE_ID = ?1", nativeQuery = true)
    void deleteArticleAuthor(Long article_id);
}
