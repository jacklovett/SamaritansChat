package com.gibsams.gibsamscoremodule.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gibsams.gibsamscoremodule.model.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

	@Override
	Optional<UserInfo> findById(Long id);

	Optional<UserInfo> findByUser(Long id);

}
