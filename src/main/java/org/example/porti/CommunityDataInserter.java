package org.example.porti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class CommunityDataInserter {

    // 각자 맞게 설정해주세요오
    private static final String DB_URL = "jdbc:mariadb://192.168.230.104:3306/web";
    private static final String DB_USER = "test";
    private static final String DB_PASSWORD = "qwer1234";

    private static final int POST_COUNT = 120;
    private static final int BATCH_SIZE = 50;

    private static final String[] CATEGORIES = {"QNA", "SHOWCASE", "CAREER", "STUDY", "FREE"};
    private static final String[] TAG_SETS = {
            "Spring,Backend,JPA",
            "Vue,Frontend,Pinia",
            "Portfolio,Design,UIUX",
            "Career,Resume,Interview",
            "Study,Algorithm,CS",
            "Java,API,MySQL"
    };

    public static void main(String[] args) {
        new CommunityDataInserter().insertData();
    }

    public void insertData() {
        System.out.println(">> 커뮤니티 더미 데이터 삽입 시작...");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            List<Long> userIds = findUserIds(conn);
            if (userIds.isEmpty()) {
                throw new IllegalStateException("user 테이블에 계정이 없습니다. 먼저 회원 데이터를 준비해주세요.");
            }

            List<CommunityRow> posts = insertCommunities(conn, userIds);
            insertComments(conn, posts, userIds);
            insertFavorites(conn, posts, userIds);
            updateCommunityCounters(conn, posts);

            conn.commit();
            System.out.println(">> [성공] 커뮤니티 더미 데이터 삽입 완료!");
        } catch (Exception e) {
            System.err.println(">> 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Long> findUserIds(Connection conn) throws SQLException {
        String sql = "SELECT idx FROM `user` WHERE enable = true ORDER BY idx";
        List<Long> ids = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getLong("idx"));
            }
        }
        return ids;
    }

    private List<CommunityRow> insertCommunities(Connection conn, List<Long> userIds) throws SQLException {
        String sql = "INSERT INTO community " +
                "(category, title, contents, tags, solved, anonymous, likes_count, comment_count, view_count, user_idx, version, create_date, update_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<CommunityRow> rows = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < POST_COUNT; i++) {
                String category = CATEGORIES[i % CATEGORIES.length];
                Long userIdx = userIds.get(i % userIds.size());
                boolean anonymous = "QNA".equals(category) && i % 6 == 0;
                LocalDateTime createdAt = LocalDateTime.now().minusDays(i % 28).minusHours(i % 12);
                LocalDateTime updatedAt = createdAt.plusHours(i % 8);
                int baseViewCount = 15 + ThreadLocalRandom.current().nextInt(180);

                pstmt.setString(1, category);
                pstmt.setString(2, buildTitle(category, i + 1));
                pstmt.setString(3, buildContents(category, i + 1));
                pstmt.setString(4, TAG_SETS[i % TAG_SETS.length]);
                pstmt.setBoolean(5, false);
                pstmt.setBoolean(6, anonymous);
                pstmt.setInt(7, 0);
                pstmt.setInt(8, 0);
                pstmt.setInt(9, baseViewCount);
                pstmt.setLong(10, userIdx);
                pstmt.setLong(11, 0L);
                pstmt.setTimestamp(12, Timestamp.valueOf(createdAt));
                pstmt.setTimestamp(13, Timestamp.valueOf(updatedAt));
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        CommunityRow row = new CommunityRow();
                        row.communityIdx = rs.getLong(1);
                        row.ownerUserIdx = userIdx;
                        row.category = category;
                        row.viewCount = baseViewCount;
                        rows.add(row);
                    }
                }

                if ((i + 1) % BATCH_SIZE == 0) {
                    conn.commit();
                    System.out.println(">> 커뮤니티 게시글 진행: " + (i + 1) + " / " + POST_COUNT);
                }
            }
        }

        conn.commit();
        return rows;
    }

    private void insertComments(Connection conn, List<CommunityRow> posts, List<Long> userIds) throws SQLException {
        String sql = "INSERT INTO community_comment (contents, community_idx, user_idx, create_date, update_date) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (CommunityRow row : posts) {
                int commentTarget = switch (row.category) {
                    case "QNA" -> ThreadLocalRandom.current().nextInt(1, 6);
                    case "SHOWCASE" -> ThreadLocalRandom.current().nextInt(1, 4);
                    case "CAREER" -> ThreadLocalRandom.current().nextInt(1, 5);
                    default -> ThreadLocalRandom.current().nextInt(0, 4);
                };

                Set<Long> commentedUsers = new HashSet<>();
                for (int i = 0; i < commentTarget; i++) {
                    Long writer = pickAnotherUser(userIds, row.ownerUserIdx, commentedUsers);
                    commentedUsers.add(writer);
                    LocalDateTime createdAt = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(0, 20)).minusMinutes(ThreadLocalRandom.current().nextInt(0, 400));

                    pstmt.setString(1, buildComment(row.category, i + 1));
                    pstmt.setLong(2, row.communityIdx);
                    pstmt.setLong(3, writer);
                    pstmt.setTimestamp(4, Timestamp.valueOf(createdAt));
                    pstmt.setTimestamp(5, Timestamp.valueOf(createdAt.plusMinutes(ThreadLocalRandom.current().nextInt(0, 40))));
                    pstmt.addBatch();

                    row.commentCount++;
                }

                if ("QNA".equals(row.category) && row.commentCount > 0) {
                    row.solved = ThreadLocalRandom.current().nextInt(100) < 65;
                }
            }
            pstmt.executeBatch();
        }
        conn.commit();
    }

    private void insertFavorites(Connection conn, List<CommunityRow> posts, List<Long> userIds) throws SQLException {
        String sql = "INSERT IGNORE INTO community_favorite (community_idx, user_idx, create_date, update_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (CommunityRow row : posts) {
                int favoriteTarget = switch (row.category) {
                    case "SHOWCASE" -> ThreadLocalRandom.current().nextInt(3, 12);
                    case "CAREER" -> ThreadLocalRandom.current().nextInt(2, 10);
                    case "QNA" -> ThreadLocalRandom.current().nextInt(1, 8);
                    default -> ThreadLocalRandom.current().nextInt(0, 6);
                };

                Set<Long> picked = new HashSet<>();
                while (picked.size() < favoriteTarget && picked.size() < userIds.size()) {
                    picked.add(userIds.get(ThreadLocalRandom.current().nextInt(userIds.size())));
                }

                for (Long userIdx : picked) {
                    LocalDateTime createdAt = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(0, 18)).minusMinutes(ThreadLocalRandom.current().nextInt(0, 180));
                    pstmt.setLong(1, row.communityIdx);
                    pstmt.setLong(2, userIdx);
                    pstmt.setTimestamp(3, Timestamp.valueOf(createdAt));
                    pstmt.setTimestamp(4, Timestamp.valueOf(createdAt.plusMinutes(ThreadLocalRandom.current().nextInt(0, 20))));
                    pstmt.addBatch();
                }
                row.likesCount = picked.size();
            }
            pstmt.executeBatch();
        }
        conn.commit();
    }

    private void updateCommunityCounters(Connection conn, List<CommunityRow> posts) throws SQLException {
        String sql = "UPDATE community SET likes_count = ?, comment_count = ?, view_count = ?, solved = ? WHERE idx = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (CommunityRow row : posts) {
                pstmt.setInt(1, row.likesCount);
                pstmt.setInt(2, row.commentCount);
                pstmt.setInt(3, row.viewCount + row.commentCount * 3 + row.likesCount * 4);
                pstmt.setBoolean(4, row.solved);
                pstmt.setLong(5, row.communityIdx);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        conn.commit();
    }

    private Long pickAnotherUser(List<Long> userIds, Long ownerUserIdx, Set<Long> excludes) {
        int safeGuard = 0;
        while (safeGuard < 1000) {
            Long candidate = userIds.get(ThreadLocalRandom.current().nextInt(userIds.size()));
            if (!candidate.equals(ownerUserIdx) && !excludes.contains(candidate)) {
                return candidate;
            }
            safeGuard++;
        }
        return ownerUserIdx;
    }

    private String buildTitle(String category, int no) {
        return switch (category) {
            case "QNA" -> "Spring/JPA 관련 질문 있습니다 " + no;
            case "SHOWCASE" -> "포트폴리오 작업물 공유합니다 " + no;
            case "CAREER" -> "취업 준비하면서 정리한 내용 공유 " + no;
            case "STUDY" -> "주말 스터디 함께하실 분 구합니다 " + no;
            default -> "개발하면서 느낀 점 적어봅니다 " + no;
        };
    }

    private String buildContents(String category, int no) {
        return switch (category) {
            case "QNA" -> "현재 개인 프로젝트에서 API와 DB를 연결하는 과정에서 고민이 생겨 질문드립니다.\n"
                    + "비슷한 경험이 있으셨던 분들의 의견을 듣고 싶습니다.\n"
                    + "질문 글 번호: " + no;
            case "SHOWCASE" -> "최근에 만든 작업물 일부를 커뮤니티에 공유합니다.\n"
                    + "UI 구성과 데이터 흐름을 중심으로 개선한 부분이 있고, 피드백 환영합니다.\n"
                    + "작업물 공유 글 번호: " + no;
            case "CAREER" -> "이력서/포트폴리오/면접 준비하면서 정리한 내용을 올립니다.\n"
                    + "저처럼 취업 준비 중인 분들에게 도움이 되었으면 좋겠습니다.\n"
                    + "커리어 글 번호: " + no;
            case "STUDY" -> "주 1회 온라인 또는 오프라인으로 공부하실 분을 찾습니다.\n"
                    + "백엔드, 알고리즘, CS 면접 대비 위주로 진행하려고 합니다.\n"
                    + "스터디 모집 글 번호: " + no;
            default -> "개발 공부하면서 느낀 점을 자유롭게 적어봅니다.\n"
                    + "작은 시행착오도 결국 경험이 된다는 생각이 들었습니다.\n"
                    + "자유 글 번호: " + no;
        };
    }

    private String buildComment(String category, int no) {
        return switch (category) {
            case "QNA" -> "비슷한 상황에서 저는 로그를 먼저 확인해봤고, 연관관계 조회 부분부터 점검했습니다. 댓글 " + no;
            case "SHOWCASE" -> "디자인이 깔끔하고 사용자 흐름도 자연스러운 것 같습니다. 댓글 " + no;
            case "CAREER" -> "정리 잘 하셨네요. 저도 면접 준비할 때 비슷하게 접근했습니다. 댓글 " + no;
            case "STUDY" -> "관심 있습니다. 일정과 진행 방식이 괜찮아 보여요. 댓글 " + no;
            default -> "공감합니다. 저도 비슷한 경험이 있어서 재미있게 읽었습니다. 댓글 " + no;
        };
    }

    private static class CommunityRow {
        private long communityIdx;
        private long ownerUserIdx;
        private String category;
        private int likesCount;
        private int commentCount;
        private int viewCount;
        private boolean solved;
    }
}