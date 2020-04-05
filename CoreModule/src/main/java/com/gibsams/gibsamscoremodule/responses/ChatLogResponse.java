package com.gibsams.gibsamscoremodule.responses;

import java.time.Instant;

import com.gibsams.gibsamscoremodule.model.ChatLog;

/**
 * 
 * @author jackl
 *
 */
public class ChatLogResponse {
	
	private Long id;
	
	private String volunteer;
	
	private String username;
	
	private int rating;
	
	private Instant startTime;
	
	private Instant endTime;
	
	private Long transcriptId;
	
	public ChatLogResponse(ChatLog chatLog) {
		this.id = chatLog.getId();
		this.volunteer = chatLog.getVolunteer();
		this.username = chatLog.getUsername();
		this.setRating(chatLog.getRating());
		this.startTime = chatLog.getStartTime();
		this.endTime = chatLog.getEndTime();
		this.transcriptId = chatLog.getTranscript().getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVolunteer() {
		return volunteer;
	}

	public void setVolunteer(String volunteer) {
		this.volunteer = volunteer;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}

	public Long getTranscriptId() {
		return transcriptId;
	}

	public void setTranscriptId(Long transcriptId) {
		this.transcriptId = transcriptId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + rating;
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((transcriptId == null) ? 0 : transcriptId.hashCode());
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
		ChatLogResponse other = (ChatLogResponse) obj;
		if (endTime == null) {
			if (other.endTime != null) {
				return false;
			}
		} else if (!endTime.equals(other.endTime)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (rating != other.rating) {
			return false;
		}
		if (startTime == null) {
			if (other.startTime != null) {
				return false;
			}
		} else if (!startTime.equals(other.startTime)) {
			return false;
		}
		if (transcriptId == null) {
			if (other.transcriptId != null) {
				return false;
			}
		} else if (!transcriptId.equals(other.transcriptId)) {
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
		return "ChatLogResponse [id=" + id + ", volunteer=" + volunteer + ", username=" + username + ", rating="
				+ rating + ", startTime=" + startTime + ", endTime=" + endTime + ", transcriptId=" + transcriptId + "]";
	}
	
}
