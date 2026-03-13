package org.example.porti.user;

import org.example.porti.user.model.EmailVerify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerifyRepository extends JpaRepository<EmailVerify, Long> {

    Optional<EmailVerify> findByUuid(String uuid);
}
