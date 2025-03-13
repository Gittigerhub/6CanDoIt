package com.sixcandoit.roomservice.repository;

import com.sixcandoit.roomservice.entity.AccountPending;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface AccountPendingRepository extends JpaRepository<AccountPending, Long> {

    @Transactional
    public void deleteByEmail(String email);

    @Transactional
    public void deleteByInsDateLessThanEqual(LocalDateTime time);

    @Query("select count(*) from AccountPending where email=:email and authenticationCode=:authenticationCode")
    public Long countByAuthenticationCodeAndEmail(String email, String authenticationCode);

}
