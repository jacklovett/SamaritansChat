package com.samaritans.samaritanscoremodule.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samaritans.samaritanscoremodule.model.ChatLog;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

	@Override
	List<ChatLog> findAll();

	@Override
	Optional<ChatLog> findById(Long id);

	boolean existsByUsername(String username);

}
