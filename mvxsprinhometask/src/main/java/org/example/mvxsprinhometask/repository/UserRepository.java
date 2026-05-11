package org.example.mvxsprinhometask.repository;

import org.example.mvxsprinhometask.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}