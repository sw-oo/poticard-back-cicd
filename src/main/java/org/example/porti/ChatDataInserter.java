package org.example.porti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatDataInserter {

    // DB 접속 정보 (본인 환경에 맞게 수정)
    private static final String URL = "jdbc:mariadb://192.100.5.102:3306/poticard";
    private static final String USER = "root";
    private static final String PASSWORD = "qwer1234";

    public static void main(String[] args) {
        ChatDataInserter inserter = new ChatDataInserter();
        inserter.insertData();
    }

    public void insertData() {
        System.out.println(">> 데이터 삽입 시작 (Pure JDBC 모드)...");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false); // 성능과 트랜잭션을 위해 수동 커밋 설정

            // 1. 채팅방 15,000개 생성
            System.out.println(">> 채팅방 생성 중...");
            String roomSql = "INSERT INTO chat_room (host_user_idx, guest_user_idx, create_date, update_date) VALUES (?, ?, NOW(), NOW())";

            List<int[]> roomList = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                for (int j = 1; j <= 15; j++) {
                    int guestIdx = i + j;
                    if (guestIdx > 1000) guestIdx -= 1000;
                    roomList.add(new int[]{Math.min(i, guestIdx), Math.max(i, guestIdx)});
                }
            }

            // 중복 제거
            List<int[]> distinctRooms = roomList.stream()
                    .map(r -> r[0] + "," + r[1])
                    .distinct()
                    .map(s -> {
                        String[] split = s.split(",");
                        return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
                    })
                    .collect(Collectors.toList());

            try (PreparedStatement pstmt = conn.prepareStatement(roomSql)) {
                for (int[] room : distinctRooms) {
                    pstmt.setInt(1, room[0]);
                    pstmt.setInt(2, room[1]);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            conn.commit();
            System.out.println(">> 채팅방 " + distinctRooms.size() + "개 삽입 완료!");

            // 2. 메시지 750,000개 생성 (방당 50개)
            System.out.println(">> 메시지 생성 중 (약 75만 건)...");
            String messageSql = "INSERT INTO chat_message (room_idx, sender_idx, contents, contents_type, is_read, create_date, update_date) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

            try (PreparedStatement pstmt = conn.prepareStatement(messageSql)) {
                int batchCount = 0;
                for (int r = 1; r <= distinctRooms.size(); r++) {
                    int[] room = distinctRooms.get(r - 1);
                    for (int m = 1; m <= 50; m++) {
                        pstmt.setInt(1, r); // room_idx (ID가 1부터 순차적이라고 가정)
                        pstmt.setInt(2, (m % 2 == 0) ? room[0] : room[1]);
                        pstmt.setString(3, "테스트 메시지 " + m);
                        pstmt.setString(4, "TEXT");
                        pstmt.setBoolean(5, true);
                        pstmt.addBatch();
                        batchCount++;

                        // 10,000개 단위로 끊어서 실행 (메모리 보호)
                        if (batchCount % 10000 == 0) {
                            pstmt.executeBatch();
                            conn.commit();
                        }
                    }
                    if (r % 1000 == 0) System.out.println(">> " + r + "번 방까지 메시지 생성 완료...");
                }
                pstmt.executeBatch(); // 남은 데이터 실행
                conn.commit();
            }

            System.out.println(">> [성공] 모든 데이터 삽입이 완료되었습니다!");

        } catch (SQLException e) {
            System.err.println(">> 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}