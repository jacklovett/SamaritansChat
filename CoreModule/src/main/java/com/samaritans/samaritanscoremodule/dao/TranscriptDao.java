package com.samaritans.samaritanscoremodule.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.samaritans.samaritanscoremodule.exception.ResourceNotFoundException;
import com.samaritans.samaritanscoremodule.model.Transcript;
import com.samaritans.samaritanscoremodule.repository.TranscriptRepository;
import com.samaritans.samaritanscoremodule.responses.TranscriptResponse;

public class TranscriptDao {

	@Autowired
	private TranscriptRepository transcriptRepository;

	public TranscriptResponse findById(Long id) {

		return transcriptRepository.findById(id).map(TranscriptResponse::new)
				.orElseThrow(() -> new ResourceNotFoundException("No transcript found with id: " + id));
	}

	public Transcript save(Transcript transcript) {
		return transcriptRepository.save(transcript);
	}
}
