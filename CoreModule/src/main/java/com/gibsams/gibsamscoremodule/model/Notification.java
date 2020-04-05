package com.gibsams.gibsamscoremodule.model;

import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.gibsams.gibsamscoremodule.utils.NotificationTypeEnum;

@Entity
@Table(name = "notifications")
public class Notification extends Audit {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7453717201948856841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String type;

	@NotBlank
	private String content;

	@Column(name = "isRead", nullable = false)
	private boolean read = false;

	@Column(name = "isProcessed", nullable = false)
	private boolean processed = false;

	@Size(max = 20)
	private String username;

	private String cta;

	@Column(name = "processed_cta")
	private String processedCTA;

	// bo user receiving notification
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Notification() {
	}

	public Notification(NotificationTypeEnum notificationTypeEnum, String username) {
		this.type = notificationTypeEnum.getType();
		this.content = StringUtils.isNotBlank(username)
				? MessageFormat.format(notificationTypeEnum.getContent(), username)
				: notificationTypeEnum.getContent();
		this.username = username;
		this.cta = notificationTypeEnum.getCta();
		this.processedCTA = notificationTypeEnum.getProcessedCTA();
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
}
