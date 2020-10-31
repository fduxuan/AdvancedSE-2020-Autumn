package com.example.scinvitepcservice.repository;

import com.example.scinvitepcservice.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author LBW
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    @Query(value = "from User where fullName = ?1")
    List<User> findByFullName(String fullName);
}
