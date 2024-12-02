package com.gym.crm.repository;

import com.gym.crm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select username from User where username LIKE :prefix")
    List<String> findByUsernamesStartedFrom(@Param("prefix") String prefix);

    @Query("select password from User where username = :username")
    String retrievePasswordByUsername(@Param("username") String username);

    @Modifying
    @Query("UPDATE User set isActive =:isActive WHERE username =:username")
    void changeIsActive(@Param("username") String username, @Param("isActive") boolean isActive);

    @Modifying
    @Query("UPDATE User set password =:password WHERE username = :username")
    void changePassword(@Param("username") String username, @Param("password") String password);

    Optional<User> findByUsername(String username);
}
