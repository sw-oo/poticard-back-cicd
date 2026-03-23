package org.example.porti.namecard;

import org.example.porti.namecard.model.Namecard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NamecardRepository extends JpaRepository<Namecard, Long>, JpaSpecificationExecutor<Namecard> {

//    @Query("SELECT n FROM Namecard n INNER JOIN FETCH n.user u WHERE u.idx = :userIdx") // 성능개선판 코드
    Optional<Namecard> findByUserIdx(@Param("userIdx") Long userIdx);

    // 방법 B: 리스트의 모든 키워드를 포함해야 하는 경우 (AND 검색 - 추천)
    // 쿼리에서 직접 루프를 돌리기 어려우므로, 서비스에서 JSON_CONTAINS를 조합해 호출하거나
    // 아래와 같이 REGEXP를 활용할 수 있습니다.
    @Query(value = "SELECT * FROM namecard n WHERE n.keywords REGEXP :pattern",
            nativeQuery = true)
    Slice<Namecard> findByAllKeywords(@Param("pattern") String pattern, Pageable pageable);

}
