package com.samaritans.samaritanscoremodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.samaritans.samaritanscoremodule.model.ChatMessage;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

	List<ChatMessage> findAllBySenderOrRecipient(String username, String recipient);

	@Query("SELECT COUNT(*) FROM ChatMessage ch WHERE ch.sender = :username AND ch.seen = 0")
	int findNumberOfUnreadMessagesByUsername(@Param("username") String username);

	@Modifying
	@Transactional
	@Query("UPDATE ChatMessage ch SET ch.seen = 1 WHERE ch.sender = :username AND ch.seen = 0")
	int updateSeenMessagesByUsername(@Param("username") String username);
}
