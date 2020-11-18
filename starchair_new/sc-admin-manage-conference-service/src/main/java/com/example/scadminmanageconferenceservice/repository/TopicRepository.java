package com.example.scadminmanageconferenceservice.repository;


import com.example.scadminmanageconferenceservice.domain.Topic;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface TopicRepository extends CrudRepository<Topic, Long> {
    @Transactional
    @Modifying
    @Query(value = "delete from TOPIC where ARTICLE_ID = ?1", nativeQuery = true)
    void deleteArticleTopic(Long article_id);
}
