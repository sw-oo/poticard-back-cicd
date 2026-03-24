package org.example.porti;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class CompanyDataInserter {

    // 각자 맞게 설정해주세요오
    private static final String DB_URL = "jdbc:mariadb://192.168.230.104:3306/web";
    private static final String DB_USER = "test";
    private static final String DB_PASSWORD = "qwer1234";

    private static final int COMPANY_USER_TARGET = 12;
    private static final int NORMAL_USER_TARGET = 40;
    private static final int COMPANY_COUNT = 80;
    private static final int BATCH_SIZE = 50;

    private static final CompanySeed[] COMPANY_SEEDS = {
            new CompanySeed("네오플래닛", "AI 기반 인재 매칭 플랫폼을 운영하는 HR 테크 스타트업입니다."),
            new CompanySeed("브릿지픽셀", "브랜딩과 데이터를 연결하는 디지털 프로덕트 회사를 지향합니다."),
            new CompanySeed("스택웨이브", "대규모 트래픽 환경에서 안정적인 SaaS를 만드는 팀입니다."),
            new CompanySeed("클라우드포지", "클라우드 인프라 자동화와 DevOps 컨설팅을 제공합니다."),
            new CompanySeed("포커스랩", "사용자 경험 중심의 제품 개발 문화를 가진 IT 기업입니다."),
            new CompanySeed("데이터크루", "데이터 분석과 추천 시스템을 서비스하는 B2B 솔루션 회사입니다."),
            new CompanySeed("모션브릭", "콘텐츠, 커머스, 플랫폼을 연결하는 풀스택 프로덕트 팀입니다."),
            new CompanySeed("아크노트", "실무형 협업 도구를 만드는 생산성 SaaS 기업입니다."),
            new CompanySeed("펄스시스템즈", "금융/공공/교육 분야의 웹 서비스를 구축하는 SI 전문 회사입니다."),
            new CompanySeed("블루메타", "AI 및 데이터 엔지니어링 역량을 기반으로 성장하는 기술 조직입니다."),
            new CompanySeed("웨이브핀", "핀테크와 데이터 파이프라인을 함께 다루는 제품팀입니다."),
            new CompanySeed("베이스캠프테크", "주니어도 빠르게 성장할 수 있는 개발 문화를 지향합니다.")
    };

    private static final JobTemplate[] JOB_TEMPLATES = {
            new JobTemplate("Backend", "백엔드 개발자", "FULL_TIME", "JUNIOR", "Java,Spring Boot,JPA,MariaDB,Redis"),
            new JobTemplate("Frontend", "프론트엔드 개발자", "FULL_TIME", "JUNIOR", "Vue.js,JavaScript,Pinia,Vite,Tailwind"),
            new JobTemplate("Fullstack", "풀스택 개발자", "FULL_TIME", "MID", "Java,Spring Boot,Vue.js,MariaDB,Docker"),
            new JobTemplate("DevOps", "DevOps 엔지니어", "FULL_TIME", "MID", "AWS,Docker,Kubernetes,GitHub Actions,Terraform"),
            new JobTemplate("AI", "AI 서비스 개발자", "FULL_TIME", "MID", "Python,FastAPI,Pytorch,Airflow,PostgreSQL"),
            new JobTemplate("Database", "데이터 엔지니어", "CONTRACT", "SENIOR", "MySQL,Redis,Kafka,Spring Batch,Python")
    };

    private static final String[] LOCATIONS = {
            "서울 강남구", "서울 성동구", "서울 송파구", "경기 성남시 판교", "서울 마포구", "원격"
    };

    public static void main(String[] args) {
        new CompanyDataInserter().insertData();
    }

    public void insertData() {
        System.out.println(">> 채용공고 더미 데이터 삽입 시작...");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            List<Long> companyUserIds = findUserIdsByRole(conn, "ROLE_COMPANY");
            List<Long> normalUserIds = findUserIdsByRole(conn, "ROLE_USER");

            if (companyUserIds.size() < COMPANY_USER_TARGET) {
                createCompanyUsers(conn, COMPANY_USER_TARGET - companyUserIds.size());
                companyUserIds = findUserIdsByRole(conn, "ROLE_COMPANY");
            }

            if (normalUserIds.size() < NORMAL_USER_TARGET) {
                createNormalUsers(conn, NORMAL_USER_TARGET - normalUserIds.size());
                normalUserIds = findUserIdsByRole(conn, "ROLE_USER");
            }

            if (companyUserIds.isEmpty()) {
                throw new IllegalStateException("ROLE_COMPANY 계정을 찾지 못했습니다.");
            }
            if (normalUserIds.isEmpty()) {
                throw new IllegalStateException("ROLE_USER 계정을 찾지 못했습니다.");
            }

            List<CompanyRow> insertedCompanies = insertCompanies(conn, companyUserIds, COMPANY_COUNT);
            insertCompanyFavorites(conn, insertedCompanies, normalUserIds);
            insertCompanyApplications(conn, insertedCompanies, normalUserIds);
            updateCompanyCounters(conn, insertedCompanies);

            conn.commit();
            System.out.println(">> [성공] 채용공고 더미 데이터 삽입 완료!");
        } catch (Exception e) {
            System.err.println(">> 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Long> findUserIdsByRole(Connection conn, String role) throws SQLException {
        String sql = "SELECT idx FROM `user` WHERE role = ? ORDER BY idx";
        List<Long> ids = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getLong("idx"));
                }
            }
        }
        return ids;
    }

    private void createCompanyUsers(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO `user` (email, name, phone, gender, address, affiliation, career, password, enable, role, created_at, updated_at, profile_image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < count; i++) {
                CompanySeed seed = COMPANY_SEEDS[i % COMPANY_SEEDS.length];
                int suffix = 7000 + i;
                pstmt.setString(1, "company" + suffix + "@porti.com");
                pstmt.setString(2, seed.name);
                pstmt.setString(3, "02-6" + String.format("%03d-%04d", i + 100, suffix));
                pstmt.setString(4, "UNKNOWN");
                pstmt.setString(5, "서울특별시 강남구 테헤란로 " + (100 + i));
                pstmt.setString(6, "인사팀");
                pstmt.setString(7, "채용 담당자");
                pstmt.setString(8, "1234");
                pstmt.setBoolean(9, true);
                pstmt.setString(10, "ROLE_COMPANY");
                pstmt.setTimestamp(11, Timestamp.valueOf(now));
                pstmt.setTimestamp(12, Timestamp.valueOf(now));
                pstmt.setString(13, "https://api.dicebear.com/9.x/shapes/svg?seed=" + seed.name + suffix);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        conn.commit();
    }

    private void createNormalUsers(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO `user` (email, name, phone, gender, address, affiliation, career, password, enable, role, created_at, updated_at, profile_image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String[] names = {"김민수", "이서연", "박지훈", "최유진", "정도윤", "한지민", "윤태호", "서은우", "장하준", "오수빈"};
        String[] affiliations = {"컴퓨터공학과", "프론트엔드 부트캠프", "백엔드 과정", "데이터 엔지니어링 과정", "AI 실무 과정"};
        String[] careers = {"신입", "1년차", "2년차", "3년차"};

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < count; i++) {
                int suffix = 8000 + i;
                String name = names[i % names.length] + suffix;
                pstmt.setString(1, "user" + suffix + "@porti.com");
                pstmt.setString(2, name);
                pstmt.setString(3, "010-9" + String.format("%03d-%04d", i + 10, suffix));
                pstmt.setString(4, i % 2 == 0 ? "MALE" : "FEMALE");
                pstmt.setString(5, "서울특별시 " + (i % 2 == 0 ? "강서구" : "송파구") + " 테스트로 " + (i + 1));
                pstmt.setString(6, affiliations[i % affiliations.length]);
                pstmt.setString(7, careers[i % careers.length]);
                pstmt.setString(8, "1234");
                pstmt.setBoolean(9, true);
                pstmt.setString(10, "ROLE_USER");
                pstmt.setTimestamp(11, Timestamp.valueOf(now));
                pstmt.setTimestamp(12, Timestamp.valueOf(now));
                pstmt.setString(13, "https://api.dicebear.com/9.x/avataaars/svg?seed=" + name);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        conn.commit();
    }

    private List<CompanyRow> insertCompanies(Connection conn, List<Long> companyUserIds, int totalCount) throws SQLException {
        String sql = "INSERT INTO company " +
                "(title, category, employment_type, experience, location, salary_min, salary_max, headcount, deadline, work_start, skills, intro, description, requirements, preferred, process, contact_email, contact_phone, remote_possible, public_open, status, applicants, new_applicants, view_count, favorite_count, user_idx, version, create_date, update_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<CompanyRow> rows = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < totalCount; i++) {
                CompanySeed companySeed = COMPANY_SEEDS[i % COMPANY_SEEDS.length];
                JobTemplate job = JOB_TEMPLATES[i % JOB_TEMPLATES.length];
                Long companyUserIdx = companyUserIds.get(i % companyUserIds.size());
                String location = LOCATIONS[i % LOCATIONS.length];
                boolean remotePossible = "원격".equals(location) || i % 4 == 0;
                boolean publicOpen = i % 7 != 0;
                String status = i % 9 == 0 ? "CLOSED" : "RECRUITING";
                LocalDate deadline = LocalDate.now().plusDays(10 + (i % 45));
                LocalDate workStart = LocalDate.now().plusDays(20 + (i % 60));
                LocalDateTime createdAt = LocalDateTime.now().minusDays(i % 35).minusHours(i % 8);
                LocalDateTime updatedAt = createdAt.plusDays(i % 4).plusHours(i % 5);

                pstmt.setString(1, buildJobTitle(companySeed.name, job.jobTitle, i + 1));
                pstmt.setString(2, job.category);
                pstmt.setString(3, job.employmentType);
                pstmt.setString(4, job.experience);
                pstmt.setString(5, location);
                pstmt.setInt(6, 3200 + (i % 6) * 400);
                pstmt.setInt(7, 4200 + (i % 6) * 600);
                pstmt.setInt(8, 1 + (i % 4));
                pstmt.setDate(9, Date.valueOf(deadline));
                pstmt.setDate(10, Date.valueOf(workStart));
                pstmt.setString(11, job.skills);
                pstmt.setString(12, buildIntro(companySeed));
                pstmt.setString(13, buildDescription(companySeed, job));
                pstmt.setString(14, buildRequirements(job));
                pstmt.setString(15, buildPreferred(job));
                pstmt.setString(16, "서류 전형 -> 직무 인터뷰 -> 컬처핏 인터뷰 -> 최종 합류");
                pstmt.setString(17, "recruit@" + normalizeEmailDomain(companySeed.name) + ".com");
                pstmt.setString(18, "02-540-" + String.format("%04d", 1000 + i));
                pstmt.setBoolean(19, remotePossible);
                pstmt.setBoolean(20, publicOpen);
                pstmt.setString(21, status);
                pstmt.setInt(22, 0);
                pstmt.setInt(23, 0);
                pstmt.setInt(24, 25 + ThreadLocalRandom.current().nextInt(220));
                pstmt.setInt(25, 0);
                pstmt.setLong(26, companyUserIdx);
                pstmt.setLong(27, 0L);
                pstmt.setTimestamp(28, Timestamp.valueOf(createdAt));
                pstmt.setTimestamp(29, Timestamp.valueOf(updatedAt));
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        CompanyRow row = new CompanyRow();
                        row.companyIdx = rs.getLong(1);
                        row.ownerUserIdx = companyUserIdx;
                        row.publicOpen = publicOpen;
                        row.status = status;
                        row.viewCount = 25 + ThreadLocalRandom.current().nextInt(220);
                        rows.add(row);
                    }
                }

                if ((i + 1) % BATCH_SIZE == 0) {
                    conn.commit();
                    System.out.println(">> 채용공고 진행: " + (i + 1) + " / " + totalCount);
                }
            }
        }

        conn.commit();
        return rows;
    }

    private void insertCompanyFavorites(Connection conn, List<CompanyRow> companies, List<Long> normalUserIds) throws SQLException {
        String sql = "INSERT IGNORE INTO company_favorite (company_idx, user_idx, create_date, update_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (CompanyRow row : companies) {
                if (!row.publicOpen) {
                    continue;
                }

                int targetCount = ThreadLocalRandom.current().nextInt(2, 10);
                Set<Long> picked = pickDistinctUsers(normalUserIds, targetCount, Set.of());
                row.favoriteCount = picked.size();

                for (Long userIdx : picked) {
                    LocalDateTime createdAt = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(0, 20));
                    pstmt.setLong(1, row.companyIdx);
                    pstmt.setLong(2, userIdx);
                    pstmt.setTimestamp(3, Timestamp.valueOf(createdAt));
                    pstmt.setTimestamp(4, Timestamp.valueOf(createdAt.plusMinutes(ThreadLocalRandom.current().nextInt(0, 120))));
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
        conn.commit();
    }

    private void insertCompanyApplications(Connection conn, List<CompanyRow> companies, List<Long> normalUserIds) throws SQLException {
        String sql = "INSERT IGNORE INTO company_application (company_idx, user_idx, create_date, update_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (CompanyRow row : companies) {
                if (!row.publicOpen) {
                    continue;
                }
                if (!"RECRUITING".equalsIgnoreCase(row.status) && !"CLOSED".equalsIgnoreCase(row.status)) {
                    continue;
                }

                int targetCount = ThreadLocalRandom.current().nextInt(1, 8);
                Set<Long> excludeOwner = new HashSet<>();
                excludeOwner.add(row.ownerUserIdx);
                Set<Long> picked = pickDistinctUsers(normalUserIds, targetCount, excludeOwner);
                row.applicants = picked.size();
                row.newApplicants = Math.min(row.applicants, ThreadLocalRandom.current().nextInt(0, 3));

                for (Long userIdx : picked) {
                    LocalDateTime createdAt = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(0, 18));
                    pstmt.setLong(1, row.companyIdx);
                    pstmt.setLong(2, userIdx);
                    pstmt.setTimestamp(3, Timestamp.valueOf(createdAt));
                    pstmt.setTimestamp(4, Timestamp.valueOf(createdAt.plusMinutes(ThreadLocalRandom.current().nextInt(0, 90))));
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
        conn.commit();
    }

    private void updateCompanyCounters(Connection conn, List<CompanyRow> companies) throws SQLException {
        String sql = "UPDATE company SET applicants = ?, new_applicants = ?, favorite_count = ?, view_count = ? WHERE idx = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (CompanyRow row : companies) {
                pstmt.setInt(1, row.applicants);
                pstmt.setInt(2, row.newApplicants);
                pstmt.setInt(3, row.favoriteCount);
                pstmt.setInt(4, row.viewCount);
                pstmt.setLong(5, row.companyIdx);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        conn.commit();
    }

    private Set<Long> pickDistinctUsers(List<Long> userIds, int count, Set<Long> excludes) {
        Set<Long> result = new HashSet<>();
        int safeGuard = 0;

        while (result.size() < count && safeGuard < 1000) {
            Long picked = userIds.get(ThreadLocalRandom.current().nextInt(userIds.size()));
            if (!excludes.contains(picked)) {
                result.add(picked);
            }
            safeGuard++;
        }
        return result;
    }

    private String buildJobTitle(String companyName, String jobTitle, int no) {
        String[] titleSuffix = {
                "신규 서비스 팀", "플랫폼 개발팀", "프로덕트팀", "데이터 조직", "코어 서비스팀", "성장 제품팀"
        };
        return companyName + " " + titleSuffix[no % titleSuffix.length] + " " + jobTitle + " 채용";
    }

    private String buildIntro(CompanySeed seed) {
        return seed.description + "\n"
                + "협업, 코드 리뷰, 문서화를 중요하게 생각하며 서비스 품질을 함께 만들어갈 분을 찾고 있습니다.";
    }

    private String buildDescription(CompanySeed seed, JobTemplate job) {
        return seed.name + "에서 " + job.jobTitle + " 포지션으로 합류하게 되면 아래 업무를 담당합니다.\n"
                + "- 신규 기능 설계 및 개발\n"
                + "- 기존 서비스 유지보수 및 성능 개선\n"
                + "- 기획/디자인/운영과의 협업\n"
                + "- 주요 기술 스택: " + job.skills;
    }

    private String buildRequirements(JobTemplate job) {
        return "- " + job.jobTitle + " 관련 기본 역량을 갖춘 분\n"
                + "- " + job.skills.replace(',', ' ') + " 기반 프로젝트 경험이 있는 분\n"
                + "- 협업과 커뮤니케이션에 익숙한 분";
    }

    private String buildPreferred(JobTemplate job) {
        return "- 테스트 코드 작성 경험이 있는 분\n"
                + "- 배포/운영 환경을 경험해 본 분\n"
                + "- " + job.category + " 영역에 대한 기술적 관심이 높은 분";
    }

    private String normalizeEmailDomain(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9가-힣]", "")
                .replaceAll("[가-힣]", "porti");
    }

    private static class CompanySeed {
        private final String name;
        private final String description;

        private CompanySeed(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    private static class JobTemplate {
        private final String category;
        private final String jobTitle;
        private final String employmentType;
        private final String experience;
        private final String skills;

        private JobTemplate(String category, String jobTitle, String employmentType, String experience, String skills) {
            this.category = category;
            this.jobTitle = jobTitle;
            this.employmentType = employmentType;
            this.experience = experience;
            this.skills = skills;
        }
    }

    private static class CompanyRow {
        private long companyIdx;
        private long ownerUserIdx;
        private boolean publicOpen;
        private String status;
        private int applicants;
        private int newApplicants;
        private int favoriteCount;
        private int viewCount;
    }
}