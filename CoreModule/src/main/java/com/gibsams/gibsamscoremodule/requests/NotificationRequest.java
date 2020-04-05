package com.gibsams.gibsamscoremodule.requests;

public class NotificationRequest {

	private Long id;
	
	private boolean read;
	
	private boolean processed;
	
	public NotificationRequest() {}
	
	public NotificationRequest(Long id, boolean read, boolean processed) {
		this.id = id;
		this.read = read;
		this.processed = processed;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (processed ? 1231 : 1237);
		result = prime * result + (read ? 1231 : 1237);
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
		NotificationRequest other = (NotificationRequest) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (processed != other.processed) {
			return false;
		}
		if (read != other.read) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NotificationRequest [id=" + id + ", read=" + read + ", processed=" + processed + "]";
	}
	
}
