package com.gibsams.gibsamscoremodule.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 
 * @author jackl
 *
 */
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }),
		@UniqueConstraint(columnNames = { "email" }) })
public class User extends Audit {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -3231012079505121556L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String username;

	@Size(max = 100)
	@Email
	private String email;

	@NotBlank
	@Size(max = 100)
	private String password;

	@Column(nullable = false)
	private boolean enabled = true;

	@Column(name = "last_active", nullable = true)
	private Instant lastActive;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@OneToOne(cascade = CascadeType.ALL)
	@JoinTable(name = "user_user_info", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "user_info_id", referencedColumnName = "id") })
	private UserInfo userInfo;

	@Column(name = "is_chat_user", nullable = false)
	private boolean chatUser;

	@OneToMany(mappedBy = "user")
	private Set<Notification> notification;

	public Set<Notification> getNotification() {
		return notification;
	}

	public void setNotification(Set<Notification> notification) {
		this.notification = notification;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Instant getLastActive() {
		return lastActive;
	}

	public void setLastActive(Instant lastActive) {
		this.lastActive = lastActive;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public boolean isChatUser() {
		return chatUser;
	}

	public void setChatUser(boolean chatUser) {
		this.chatUser = chatUser;
	}

}
