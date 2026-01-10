package com.priyansh.notification_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.priyansh.notification_system.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
