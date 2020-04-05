package com.gibsams.gibsamscoremodule.requests;

public class ChatLogRequest {
	
	private String volunteer;
	
	private String username;
	
	private int rating;
	
	private String notes;

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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		ChatLogRequest other = (ChatLogRequest) obj;
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
		return "ChatLogRequest [volunteer=" + volunteer + ", username=" + username + ", rating=" + rating + ", notes="
				+ notes + "]";
	}

}
