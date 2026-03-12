package org.example.porti.namecard;

import org.example.porti.namecard.model.Namecard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NamecardRepository extends JpaRepository<Namecard, Long> {

//    @Query("SELECT n FROM Namecard n INNER JOIN FETCH n.user u WHERE u.idx = :userIdx") // 성능개선판 코드
    Optional<Namecard> findByUserIdx(@Param("userIdx") Long userIdx);
}
