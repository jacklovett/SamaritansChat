package com.gibsams.gibsamscoremodule.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.Transcript;
import com.gibsams.gibsamscoremodule.repository.TranscriptRepository;
import com.gibsams.gibsamscoremodule.responses.TranscriptResponse;

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
