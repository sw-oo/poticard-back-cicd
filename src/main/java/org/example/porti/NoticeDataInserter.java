package org.example.porti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class NoticeDataInserter {

    // 각자 맞게 설정해주세요오
    private static final String DB_URL = "jdbc:mariadb://192.168.230.104:3306/web";
    private static final String DB_USER = "test";
    private static final String DB_PASSWORD = "qwer1234";

    private static final List<NoticeSeed> NOTICE_SEEDS = List.of(
            new NoticeSeed(
                    "[공지] 포티카드 커뮤니티 운영 정책 안내",
                    "안녕하세요. 포티카드 운영팀입니다.\n\n커뮤니티 이용 시 타인을 비방하거나 부적절한 표현이 포함된 게시글은 사전 안내 없이 숨김 또는 삭제될 수 있습니다.\n질문/답변, 취업/커리어, 작품 공유 등 각 카테고리에 맞는 글을 작성해 주시기 바랍니다.\n\n보다 건강한 커뮤니티를 위해 이용자 여러분의 협조를 부탁드립니다."
            ),
            new NoticeSeed(
                    "[안내] 채용 공고 노출 방식 일부 개선 예정",
                    "채용 페이지에서 공개/비공개 공고 구분과 지원 상태 표시가 더 명확하게 보이도록 개선 작업이 진행될 예정입니다.\n\n적용 이후에는 공개 공고만 개인 사용자에게 노출되며, 기업 페이지에서는 비공개 공고를 별도로 확인할 수 있습니다.\n서비스 이용에 참고 부탁드립니다."
            ),
            new NoticeSeed(
                    "[공지] 명함 기능 업데이트 안내",
                    "내 명함 불러오기 기능과 명함 수정 후 반영 속도를 개선했습니다.\n\n홈 화면과 커뮤니티 화면에서 명함 정보를 더 안정적으로 불러올 수 있도록 순차 반영 중입니다.\n문제가 지속되면 관리자에게 문의해 주세요."
            ),
            new NoticeSeed(
                    "[안내] 포트폴리오 및 커뮤니티 점검 일정",
                    "이번 주 금요일 오전 02:00 ~ 04:00 동안 포트폴리오/커뮤니티 관련 점검이 예정되어 있습니다.\n\n점검 시간 동안 일부 기능이 일시적으로 느리거나 접속이 제한될 수 있습니다.\n이용에 불편을 드려 죄송합니다."
            ),
            new NoticeSeed(
                    "[이벤트] 신규 가입자 명함 등록 이벤트",
                    "신규 가입 후 명함을 등록한 사용자 대상으로 소정의 리워드 이벤트를 진행합니다.\n\n프로필과 명함을 함께 완성하면 채용 추천 및 커뮤니티 활동에 더 많은 노출 기회를 얻을 수 있습니다.\n자세한 내용은 추후 별도 공지를 참고해 주세요."
            ),
            new NoticeSeed(
                    "[공지] 지원 내역 및 즐겨찾기 기능 안내",
                    "채용 공고 페이지에서 지원하기/지원취소/즐겨찾기 기능을 사용할 수 있습니다.\n\n지원 내역은 개인 사용자 기준으로 관리되며, 기업 페이지에서는 지원자 목록을 통해 확인 가능합니다.\n혼선을 막기 위해 기능 사용 전 최신 화면으로 새로고침 후 이용해 주세요."
            )
    );

    public static void main(String[] args) {
        new NoticeDataInserter().insertData();
    }

    public void insertData() {
        System.out.println(">> 공지사항 더미 데이터 삽입 시작...");

        String sql = "INSERT INTO notice (title, content, create_date, update_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (int i = 0; i < NOTICE_SEEDS.size(); i++) {
                NoticeSeed seed = NOTICE_SEEDS.get(i);
                LocalDateTime createdAt = LocalDateTime.now().minusDays((NOTICE_SEEDS.size() - i) * 2L);
                LocalDateTime updatedAt = createdAt.plusHours(2);

                pstmt.setString(1, seed.title);
                pstmt.setString(2, seed.content);
                pstmt.setTimestamp(3, Timestamp.valueOf(createdAt));
                pstmt.setTimestamp(4, Timestamp.valueOf(updatedAt));
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
            System.out.println(">> [성공] 공지사항 더미 데이터 삽입 완료!");
        } catch (SQLException e) {
            System.err.println(">> 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class NoticeSeed {
        private final String title;
        private final String content;

        private NoticeSeed(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }
}