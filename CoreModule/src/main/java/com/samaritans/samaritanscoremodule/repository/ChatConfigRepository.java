package com.samaritans.samaritanscoremodule.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samaritans.samaritanscoremodule.model.ChatConfig;

@Repository
public interface ChatConfigRepository extends JpaRepository<ChatConfig, Long> {

	Optional<ChatConfig> findFirstByOrderByIdAsc();

	Optional<ChatConfig> findById(int id);

}
