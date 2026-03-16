package org.example.porti;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatDataInserter {

    private static final String DB_URL = "jdbc:mariadb://192.100.5.102:3306/poticard";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "qwer1234";

    public static void main(String[] args) {
        System.out.println("🚀 데이터 재삽입 시작 (안정적인 2단계 방식)");
        long startTime = System.currentTimeMillis();

        String insertRoomSql = "INSERT INTO chat_room (host_user_idx, guest_user_idx, create_date, update_date) VALUES (?, ?, ?, ?)";
        String insertMessageSql = "INSERT INTO chat_message (contents, contents_type, is_read, room_idx, sender_idx, create_date, update_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            Long myIdx = 1L;

            // 1단계: 방 999개 생성 및 생성된 IDX 수집
            List<Long> roomIndices = new ArrayList<>();
            List<Long> opponentIndices = new ArrayList<>();

            try (PreparedStatement roomStmt = conn.prepareStatement(insertRoomSql, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 2; i <= 1000; i++) {
                    roomStmt.setLong(1, myIdx);
                    roomStmt.setLong(2, (long) i);
                    roomStmt.setTimestamp(3, now);
                    roomStmt.setTimestamp(4, now);
                    roomStmt.addBatch();
                    opponentIndices.add((long) i);
                }
                roomStmt.executeBatch();

                // 생성된 모든 방의 PK를 리스트에 담음
                ResultSet rs = roomStmt.getGeneratedKeys();
                while (rs.next()) {
                    roomIndices.add(rs.getLong(1));
                }
            }
            System.out.println("✅ 채팅방 " + roomIndices.size() + "개 생성 완료.");

            // 2단계: 수집된 방 IDX를 바탕으로 메시지 99,900개 생성
            int totalMsgCount = 0;
            try (PreparedStatement msgStmt = conn.prepareStatement(insertMessageSql)) {
                for (int k = 0; k < roomIndices.size(); k++) {
                    long roomIdx = roomIndices.get(k);
                    long opponentIdx = opponentIndices.get(k);

                    for (int j = 1; j <= 100; j++) {
                        msgStmt.setString(1, "방 " + roomIdx + "의 " + j + "번째 메시지");
                        msgStmt.setString(2, "TEXT");
                        msgStmt.setBoolean(3, j <= 90);
                        msgStmt.setLong(4, roomIdx);
                        msgStmt.setLong(5, (j % 2 == 0) ? myIdx : opponentIdx);
                        msgStmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now().minusMinutes(100 - j)));
                        msgStmt.setTimestamp(7, now);

                        msgStmt.addBatch();
                        totalMsgCount++;

                        // 2000개 단위로 확실하게 끊어서 전송
                        if (totalMsgCount % 2000 == 0) {
                            msgStmt.executeBatch();
                        }
                    }
                }
                msgStmt.executeBatch(); // 남은 메시지 전송
            }

            conn.commit();
            long endTime = System.currentTimeMillis();
            System.out.println("✅ 최종 완료! 메시지 총합: " + totalMsgCount + "건");
            System.out.println("⏱ 소요 시간: " + (endTime - startTime) + "ms");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}