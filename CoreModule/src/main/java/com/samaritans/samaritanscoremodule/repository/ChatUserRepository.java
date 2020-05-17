package com.samaritans.samaritanscoremodule.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samaritans.samaritanscoremodule.model.ChatUser;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

	Optional<ChatUser> findByUsernameOrEmail(String username, String email);

	boolean existsByUsername(String username);
}