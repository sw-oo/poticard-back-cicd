package org.example.porti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DummyDataInserter {

    // 1. DB 접속 정보 세팅 (본인의 DB 환경에 맞게 수정하세요)
    // 팁: MySQL의 경우 rewriteBatchedStatements=true 옵션을 넣으면 Insert 속도가 비약적으로 상승합니다.
    // private static final String DB_URL = "jdbc:mariadb://10.10.10.30:3306/test";
    private static final String DB_URL = "jdbc:mariadb://10.10.10.30:3306/test";
    private static final String DB_USER = "root"; // 예: root
    private static final String DB_PASSWORD = "qwer1234"; // 예: 1234

    public static void main(String[] args) {
        System.out.println("🚀 더미 데이터 10,000건 삽입을 시작합니다...");
        long startTime = System.currentTimeMillis();

        // User 테이블 (JPA 예약어 충돌 방지를 위해 백틱 ` 사용)
        String insertUserSql = "INSERT INTO `user` (email, name, phone, gender, address, affiliation, career, password, enable, role, created_at, updated_at, profile_image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Namecard 테이블
        String insertNamecardSql = "INSERT INTO namecard (title, layout, color, url, keywords, created_at, updated_at, user_idx, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement userStmt = conn.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement namecardStmt = conn.prepareStatement(insertNamecardSql)) {

            // Auto-commit을 끄고 수동으로 트랜잭션을 관리하여 속도 향상
            conn.setAutoCommit(false);


            int totalCount = 1000;
            int batchSize = 100; // 1000건씩 끊어서 DB에 전송 (메모리 부족 방지)
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            for (int i = 1; i <= totalCount; i++) {
                // ----------------------------------------------------
                // 1. User 데이터 파라미터 세팅
                // ----------------------------------------------------
                userStmt.setString(1, "user" + i + "@example.com"); // email
                userStmt.setString(2, "테스터" + i); // name
                userStmt.setString(3, "010-0000-" + String.format("%04d", i)); // phone
                userStmt.setString(4, i % 2 == 0 ? "MALE" : "FEMALE"); // gender
                userStmt.setString(5, "서울특별시 강남구 테헤란로 " + i); // address
                userStmt.setString(6, "포티 컴퍼니"); // affiliation
                userStmt.setString(7, i + "년차 개발자"); // career
                userStmt.setString(8, "password" + i); // password
                userStmt.setBoolean(9, true); // enable
                userStmt.setString(10, "ROLE_USER"); // role
                userStmt.setTimestamp(11, now); // created_at
                userStmt.setTimestamp(12, now); // updated_at
                userStmt.setString(13, "http://cdn.testprofileimage.api/"+String.format("%04d", i)); // profile image



                userStmt.addBatch(); // User 배치에 추가

                // ----------------------------------------------------
                // 2. Namecard 데이터 파라미터 세팅
                // ----------------------------------------------------
                namecardStmt.setString(1, "테스터" + i + "의 명함"); // title
                namecardStmt.setString(2, "LAYOUT_A"); // layout
                namecardStmt.setString(3, i % 2 == 0 ? "BLUE" : "BLACK"); // color
                namecardStmt.setString(4, "https://porti.example.com/user" + i); // url
                namecardStmt.setString(5, "[\"Java\",\"Spring\",\"Backend\"]");
                namecardStmt.setTimestamp(6, now); // created_at
                namecardStmt.setTimestamp(7, now); // updated_at
                namecardStmt.setLong(8, i);// user_idx
                namecardStmt.setString(9, "로비바보");


                namecardStmt.addBatch(); // Namecard 배치에 추가

                // ----------------------------------------------------
                // 3. 1,000건마다 DB에 쿼리 전송 (OutOfMemory 방지)
                // ----------------------------------------------------
                if (i % batchSize == 0) {
                    userStmt.executeBatch();
                    namecardStmt.executeBatch();
                    System.out.println("진행 상황: " + i + " / " + totalCount + " 건 처리 완료");
                }
            }

            // 커밋하여 실제 DB에 반영
            conn.commit();

            long endTime = System.currentTimeMillis();
            System.out.println("✅ 10,000건 삽입 완료! 소요 시간: " + (endTime - startTime) + "ms");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ 데이터 삽입 중 오류가 발생했습니다. DB 정보와 테이블 구조를 확인해주세요.");
        }
    }
}