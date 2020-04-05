package com.gibsams.gibsamscoremodule.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gibsams.gibsamscoremodule.model.ChatConfig;

@Repository
public interface ChatConfigRepository extends JpaRepository<ChatConfig, Long> {

	Optional<ChatConfig> findFirstByOrderByIdAsc();

	Optional<ChatConfig> findById(int id);

}
