package com.gibsams.gibsamscoremodule.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gibsams.gibsamscoremodule.model.BoUser;

@Repository
public interface BoUserRepository extends JpaRepository<BoUser, Long> {

	@Override
	List<BoUser> findAll();

	Optional<BoUser> findByUsernameOrEmail(String username, String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

}
