package com.samaritans.samaritanscoremodule.responses;

import com.samaritans.samaritanscoremodule.requests.Message;

public class TranscriptMessage {

	private String dateSent;
	
	private String sender;
	
	private String recipient;
	
	private String content;
	
	public TranscriptMessage(Message message) {
		this.dateSent = message.getDateSent().toString();
		this.sender = message.getSender();
		this.recipient = message.getRecipient();
		this.content = message.getContent();
	}

	public String getDateSent() {
		return dateSent;
	}

	public void setDateSent(String dateSent) {
		this.dateSent = dateSent;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((dateSent == null) ? 0 : dateSent.hashCode());
		result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
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
		TranscriptMessage other = (TranscriptMessage) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		if (dateSent == null) {
			if (other.dateSent != null) {
				return false;
			}
		} else if (!dateSent.equals(other.dateSent)) {
			return false;
		}
		if (recipient == null) {
			if (other.recipient != null) {
				return false;
			}
		} else if (!recipient.equals(other.recipient)) {
			return false;
		}
		if (sender == null) {
			if (other.sender != null) {
				return false;
			}
		} else if (!sender.equals(other.sender)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TranscriptMessage [dateSent=" + dateSent + ", sender=" + sender + ", recipient=" + recipient
				+ ", content=" + content + "]";
	}
	
}
