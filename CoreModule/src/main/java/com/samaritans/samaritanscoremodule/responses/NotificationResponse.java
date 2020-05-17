package com.samaritans.samaritanscoremodule.responses;

import com.samaritans.samaritanscoremodule.model.Notification;

public class NotificationResponse implements Comparable<NotificationResponse>{

	private Long id;
	
	private String type;
	
	private String content;
	
	private boolean read;
	
	private boolean processed;
	
	private String username;
	
	private String volunteer;
	
	private String cta;

	private String processedCTA;

	public NotificationResponse(Notification notification) {
		this.id = notification.getId();
		this.type = notification.getType();
		this.content = notification.getContent();
		this.read = notification.isRead();
		this.processed = notification.isProcessed();
		this.username = notification.getUsername();
		this.volunteer = notification.getUser() != null ? notification.getUser().getUsername() : null;
		this.cta = notification.getCta();
		this.processedCTA = notification.getProcessedCTA();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
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

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	public String getCta() {
		return cta;
	}

	public void setCta(String cta) {
		this.cta = cta;
	}
	
	public String getProcessedCTA() {
		return processedCTA;
	}

	public void setProcessedCTA(String processedCTA) {
		this.processedCTA = processedCTA;
	}
	
	@Override
	public int compareTo(NotificationResponse n) {
		return this.getId().compareTo(n.getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((cta == null) ? 0 : cta.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (processed ? 1231 : 1237);
		result = prime * result + ((processedCTA == null) ? 0 : processedCTA.hashCode());
		result = prime * result + (read ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		NotificationResponse other = (NotificationResponse) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		if (cta == null) {
			if (other.cta != null) {
				return false;
			}
		} else if (!cta.equals(other.cta)) {
			return false;
		}
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
		if (processedCTA == null) {
			if (other.processedCTA != null) {
				return false;
			}
		} else if (!processedCTA.equals(other.processedCTA)) {
			return false;
		}
		if (read != other.read) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
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
		return "NotificationResponse [id=" + id + ", type=" + type + ", content=" + content + ", read=" + read
				+ ", processed=" + processed + ", username=" + username + ", volunteer=" + volunteer + ", cta=" + cta
				+ ", processedCTA=" + processedCTA + "]";
	}

}
