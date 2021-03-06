package com.samaritans.samaritanscoremodule.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.samaritans.samaritanscoremodule.requests.Message;

/**
 * 
 * @author jackl
 *
 */
@Entity
@Table(name = "chat_message")
public class ChatMessage extends DateAudit {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 2919204134287700112L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String sender;

	@NotBlank
	@Size(max = 20)
	private String recipient;

	@NotBlank
	private String content;

	@Column(name = "isRead", nullable = false)
	private boolean read = false;

	public ChatMessage() {
	}

	public ChatMessage(Message message) {
		this.sender = message.getSender();
		this.recipient = message.getRecipient();
		this.content = message.getContent();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

}
