package com.gibsams.gibsamscoremodule.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gibsams.gibsamscoremodule.model.Transcript;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, Long> {

	@Override
	Optional<Transcript> findById(Long id);

}
