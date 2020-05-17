package com.samaritans.samaritanscoremodule.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Db entity for configurable settings for the chat service
 * 
 * @author jackl
 *
 */
@Entity
@Table(name = "chat_config")
public class ChatConfig extends Audit {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -4803115932428279067L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "is_time_restricted")
	private boolean timeRestricted;

	@Column(name = "available_from")
	private int availableFrom;

	@Column(name = "available_until")
	private int availableUntil;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isTimeRestricted() {
		return timeRestricted;
	}

	public void setTimeRestricted(boolean timeRestricted) {
		this.timeRestricted = timeRestricted;
	}

	public int getAvailableFrom() {
		return availableFrom;
	}

	public void setAvailableFrom(int availableFrom) {
		this.availableFrom = availableFrom;
	}

	public int getAvailableUntil() {
		return availableUntil;
	}

	public void setAvailableUntil(int availableUntil) {
		this.availableUntil = availableUntil;
	}

}
