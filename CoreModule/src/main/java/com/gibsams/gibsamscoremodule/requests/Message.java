package com.gibsams.gibsamscoremodule.requests;

import java.time.Instant;

import com.gibsams.gibsamscoremodule.model.ChatMessage;
import com.gibsams.gibsamscoremodule.utils.MessageType;

/**
 * DTO for messages sent to frontend
 * @author jackl
 *
 */
public class Message {
	
	private Instant dateSent;
	
	private String sender;
	
	private String recipient;
	
	private String content;
	
	private MessageType type;
	
	public Message() {}
	
	public Message(ChatMessage chatMessage) {
		this.dateSent = chatMessage.getDateCreated();
		this.sender = chatMessage.getSender();
		this.recipient = chatMessage.getRecipient();
		this.content = chatMessage.getContent();
		this.type = MessageType.CHAT;
	}

	public Instant getDateSent() {
		return dateSent;
	}

	public void setDateSent(Instant dateSent) {
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

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((dateSent == null) ? 0 : dateSent.hashCode());
		result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Message other = (Message) obj;
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
		if (type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Message [dateSent=" + dateSent + ", sender=" + sender + ", recipient=" + recipient + ", content="
				+ content + ", type=" + type + "]";
	}

}
