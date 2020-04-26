package com.gibsams.gibsamscoremodule.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "chat_users", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }),
		@UniqueConstraint(columnNames = { "email" }) })
public class ChatUser extends User {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -3460874809159442332L;

}
