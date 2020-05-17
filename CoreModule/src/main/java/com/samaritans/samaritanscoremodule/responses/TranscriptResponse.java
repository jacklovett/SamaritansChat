package com.samaritans.samaritanscoremodule.responses;

import com.samaritans.samaritanscoremodule.model.Transcript;

public class TranscriptResponse {
	
	private Long id;
	private String notes;
	private String conversation;
	private String username;
	private String volunteer;
	private int rating;
	
	public TranscriptResponse() { }
	
	public TranscriptResponse(Transcript transcript) {
		this.id = transcript.getId();
		this.notes = transcript.getNotes();
		this.conversation = transcript.getConversation();
		this.username = transcript.getChatLog().getUsername();
		this.volunteer = transcript.getChatLog().getVolunteer();
		this.setRating(transcript.getChatLog().getRating());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getConversation() {
		return conversation;
	}

	public void setConversation(String conversation) {
		this.conversation = conversation;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVolunteer() {
		return volunteer;
	}

	public void setVolunteer(String volunteer) {
		this.volunteer = volunteer;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conversation == null) ? 0 : conversation.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + rating;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((volunteer == null) ? 0 : volunteer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TranscriptResponse other = (TranscriptResponse) obj;
		if (conversation == null) {
			if (other.conversation != null) {
				return false;
			}
		} else if (!conversation.equals(other.conversation)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (notes == null) {
			if (other.notes != null) {
				return false;
			}
		} else if (!notes.equals(other.notes)) {
			return false;
		}
		if (rating != other.rating) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		if (volunteer == null) {
			if (other.volunteer != null) {
				return false;
			}
		} else if (!volunteer.equals(other.volunteer)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TranscriptResponse [id=" + id + ", notes=" + notes + ", conversation=" + conversation + ", username="
				+ username + ", volunteer=" + volunteer + ", rating=" + rating + "]";
	}

}
