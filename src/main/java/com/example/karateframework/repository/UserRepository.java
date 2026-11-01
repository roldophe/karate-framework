package com.example.karateframework.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.karateframework.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByActive(boolean active);

    @Query("SELECT u FROM User u WHERE u.active = :active ORDER BY u.createdAt DESC")
    List<User> findActiveUsersOrderedByCreationDate(@Param("active") boolean active);

    @Query(
            "SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%',"
                    + " :searchTerm, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%',"
                    + " :searchTerm, '%'))")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
