package com.samaritans.samaritanscoremodule.model;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "dateCreated", "dateModified" }, allowGetters = true)
public class DateAudit implements Serializable {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -4178484563757014592L;

	@CreatedDate
	@Column(name = "date_created", nullable = false, updatable = false)
	private Instant dateCreated;

	@LastModifiedDate
	@Column(name = "date_modified", nullable = false)
	private Instant dateModified;

	public Instant getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Instant dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Instant getDateModified() {
		return dateModified;
	}

	public void setDateModified(Instant dateModified) {
		this.dateModified = dateModified;
	}

}