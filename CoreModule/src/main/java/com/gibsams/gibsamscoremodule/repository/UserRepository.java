package com.gibsams.gibsamscoremodule.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gibsams.gibsamscoremodule.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Override
	List<User> findAll();

	List<User> findByUserInfoNotNull();

	Optional<User> findByEmail(String email);

	Optional<User> findByUsernameOrEmail(String username, String email);

	Optional<User> findByUsername(String username);

	@Override
	Optional<User> findById(Long id);

	List<User> findByIdIn(List<Long> userIds);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}