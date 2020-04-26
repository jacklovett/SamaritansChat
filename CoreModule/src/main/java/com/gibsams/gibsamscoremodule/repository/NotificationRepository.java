package com.gibsams.gibsamscoremodule.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gibsams.gibsamscoremodule.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("SELECT n FROM Notification n WHERE n.boUser.id = :userId ORDER BY id DESC")
	List<Notification> findAllByUserByOrderByIdDesc(@Param("userId") Long userId);

	@Override
	Optional<Notification> findById(Long id);

	@Modifying
	@Transactional
	@Query("DELETE FROM Notification n WHERE n.processed = 1 AND n.read = 1")
	void deleteReadAndProcessedNotifications();

}
