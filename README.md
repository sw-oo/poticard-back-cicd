<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=rounded&color=FFEB00&height=220&section=header&text=🗂️%20Poticard&fontSize=58&fontColor=000000&animation=twinkling&fontAlignY=40&desc=나만의%20커리어를%20한%20장의%20카드로&descAlignY=65&descSize=20" width="100%" />
  <h3 align="center">나만의 포트폴리오 조합으로 완성하는 단 한 장의 명함</h3>
  <p align="center">지원자와 채용 담당자를 가장 효율적으로 연결하는 디지털 명함 플랫폼 Poticard를 소개합니다.</p>
</p>

---

## 👥 팀원

<div align="center">

| **김민규** | **이성재** | **전성훈** |                                                   **최승우우**                                                    |
| :---: | :---: | :---: |:-------------------------------------------------------------------------------------------------------------:|
| <img src="https://avatars.githubusercontent.com/u/244694024?v=4" width="120" style="border-radius:50%"/> | <img src="https://avatars.githubusercontent.com/u/245795542?s=64&v=4" width="120" style="border-radius:50%"/> | <img src="https://avatars.githubusercontent.com/u/153381713?s=64&v=4" width="120" style="border-radius:50%"/> | <img src="https://avatars.githubusercontent.com/u/140137784?s=64&v=4" width="120" style="border-radius:50%"/> |
| [@luel1018](https://github.com/luel1018) | [@Tahcy-99](https://github.com/Tahcy-99) | [@1jshun](https://github.com/1jshun) |                                      [@sw-oo](https://github.com/sw-oo)                                       |

</div>

---

## 📌 목차 (Table of Contents)
### 1. [🔗 바로가기](#-바로가기)
### 2. [🔨 기술 스택](#-기술-스택)
### 3. [⚙️ 시스템 아키텍처](#-시스템-아키텍처)
### 4. [📘 프로젝트 소개](#-프로젝트-소개)
### 5. [📑 핵심 도메인 및 서비스 명세](#-핵심-도메인-및-서비스-명세)
### 6. [🚧 인프라 및 설정](#-인프라-및-설정-infrastructure)

---

## 🔗 바로가기

| 구분             | 링크                                                                              |
|:---------------|:----------------------------------------------------------------------------------|
| **🌐 홈페이지**    | [www.poticard.kro.kr](https://www.poticard.kro.kr)                                |
| **📖 API 명세서** | [Swagger UI](https://api.poticard.kro.kr/swagger-ui/index.html)                   |
| **📖 상세 설명**   | [Poticard WIKI](https://github.com/beyond-sw-camp/be24-3rd-DevOops-Poticard/wiki) |
---

## 🔨️ 기술 스택

### 💻 Development
| Layer | Technologies                                                                                                                                                                                                        |
| :--- |:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Backend** | ![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=java&logoColor=white) ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=spring&logoColor=white)                   |
| **Frontend** | ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black) ![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=flat-square&logo=vue.js&logoColor=white) |
| **Database** | ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)                                                                                                              |

### 🚀 Infrastructure & DevOps
| Category | Technologies                                                                                                                                                                                                                                                                                                                                                                                             |
| :--- |:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **DevOps** | ![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=nginx&logoColor=white)                                                                                                                                                                                                                                                                                                         |
| **Monitoring** | ![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat-square&logo=grafana&logoColor=white) ![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=flat-square&logo=prometheus&logoColor=white) ![Jaeger](https://img.shields.io/badge/Jaeger-60D0E4?style=flat-square&logo=jaegertracing&logoColor=white)                                                                        |
| **Tools** |  ![Git](https://img.shields.io/badge/Git-F05032?style=flat-square&logo=git&logoColor=white) ![Postman](https://img.shields.io/badge/Postman-FF6C37?style=flat-square&logo=postman&logoColor=white) ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)                                                                                                |

---

## ⚙️ 시스템 아키텍처
<img width="668" height="545" alt="시스템 아키텍처 drawio" src="https://github.com/user-attachments/assets/eeb9a8cd-cc44-42ed-aaaa-fc0835bd06f2" />

---

## 📘 프로젝트 소개

PotiCard 서비스는 사용자의 경력 및 업력을 분석하여 구직·이직용 포트폴리오 작성을 지원하고, 핵심 키워드를 포함한 디지털 명함을 생성·관리하는 웹 서비스입니다.

사용자는 자신의 경험과 프로젝트 내용을 포트폴리오로 작성할 수 있으며, 시스템은 이를 분석하여 주요 기술 스택과 역량을 자동으로 도출합니다. 이를 통해 사용자는 자신의 전문성을 보다 명확하게 정리하고, 효율적인 자기 표현 수단으로 활용할 수 있습니다.

또한 기업은 본 서비스를 통해 구직자의 디지털 명함과 포트폴리오를 확인함으로써 지원자의 핵심 역량을 신속하게 파악하고, 이를 기반으로 채용 및 인재 컨택 과정에 활용할 수 있습니다.

---


## 📑 핵심 도메인 및 서비스 명세
### 📌 상세 내용은 우측의 "🔗" 아이콘을 클릭하여 Wiki에서 확인하실 수 있습니다.

Poticard 프로젝트의 핵심 비즈니스 로직을 담당하는 주요 도메인 서비스입니다. 각 도메인은 독립적인 역할을 수행하며, 유기적으로 연결되어 통합된 플랫폼 경험을 제공합니다.

### 👤 1. User (사용자 계정 및 인증) [🔗](https://github.com/beyond-sw-camp/be24-3rd-DevOops-Poticard/wiki/User-Management)
> **플랫폼의 기반이 되는 사용자 관리 및 보안 도메인입니다.**

* **권한 분리**: 일반 유저(`ROLE_USER`)와 기업 유저(`ROLE_COMPANY`)로 역할을 명확히 구분하여 맞춤형 서비스를 제공합니다.
* **보안 및 인증**: JWT(JSON Web Token) 기반의 안전한 인증 시스템을 구축하였으며, Google 및 Kakao OAuth2 소셜 로그인과 이메일 인증 기반의 로컬 회원가입을 모두 지원합니다.
* **프로필 관리**: 가입 시 DiceBear API를 활용한 기본 아바타가 제공되며, 사용자의 연락처, 소속, 경력 등의 세부 정보를 유연하게 관리할 수 있습니다.

### 🪪 2. Namecard (디지털 명함) [🔗](https://github.com/beyond-sw-camp/be24-3rd-DevOops-Poticard/wiki/Namecard)
> **사용자의 핵심 아이덴티티와 기술 스택을 한눈에 보여주는 디지털 명함 서비스입니다.**

* **1:1 맞춤형 명함**: 사용자당 1개의 고유한 명함을 보유하며, 개인의 개성을 살릴 수 있도록 테마, 색상, 레이아웃 커스터마이징을 지원합니다.
* **핵심 정보 압축**: GitHub, 블로그 등의 대표 링크와 주로 사용하는 기술 스택 키워드를 직관적으로 배치하여 네트워킹의 효율성을 높입니다.

### 📇 3. Portfolio (포트폴리오 작성 및 AI 첨삭) [🔗](https://github.com/beyond-sw-camp/be24-3rd-DevOops-Poticard/wiki/Portfolio)
> **사용자가 포트폴리오를 작성, 첨삭, 스타일 설정, 조회를 할 수 있습니다.**

* **유연한 에디팅**: 여러 개의 포트폴리오를 목적에 맞게 생성할 수 있으며, `Section` 단위로 본문을 분할하고 순서를 자유롭게 재배치할 수 있습니다.
* **자동 키워드 추출**: 작성된 본문을 AI가 분석하여 사용된 프레임워크나 데이터베이스 등의 핵심 기술 스택을 자동으로 추출하고 태깅합니다.
* **✨ AI 스마트 어시스턴트 (PRO 전용)**:
    * **AI 첨삭**: Google Gemini API를 연동하여 사용자가 작성한 문장을 더욱 전문적이고 매끄러운 실무 언어로 교정해 줍니다.

### 🏢 4. Company (기업 채용 및 구인구직) [🔗](https://github.com/beyond-sw-camp/be24-3rd-DevOops-Poticard/wiki/Community)
> **기업과 인재를 연결해 주는 맞춤형 채용 파이프라인입니다.**

* **상세 채용 공고**: 기업 유저는 기술 스택, 연봉, 원격 근무 여부 등 상세한 조건이 포함된 채용 공고를 등록할 수 있습니다.
* **지원 및 트래킹**: 일반 유저는 탭 한 번으로 자신의 포티카드(명함/포트폴리오)를 첨부하여 지원할 수 있으며, 기업은 지원자 목록을 효율적으로 관리할 수 있습니다.
* **스마트 추천**: 조회수와 즐겨찾기(스크랩) 데이터를 기반으로 유저에게 적합한 인기 채용 공고를 추천합니다.

### 👪 5. Community (개발자 소통 채널) [🔗](https://github.com/beyond-sw-camp/be24-3rd-DevOops-Poticard/wiki/Community)
> **개발자 간의 지식 공유, 네트워킹, 문제 해결을 위한 커뮤니티 공간입니다.**

* **카테고리 분류**: 일반적인 네트워킹 게시글뿐만 아니라, 질문 및 답변(Q&A) 기능을 지원합니다.
* **문제 해결 트래킹**: Q&A 게시글의 경우 유효한 답변이 달리면 해결 완료 상태로 전환하여 정보의 품질을 높입니다.
* **인기글 시스템**: 좋아요 수, 댓글 수, 조회수 등의 인터랙션 지표를 종합하여 실시간 랭킹 및 인기글을 제공합니다.

### 💬 6. Chat (실시간 채팅 및 화상 통화) [🔗](https://github.com/beyond-sw-camp/be24-3rd-DevOops-Poticard/wiki/Chat-and-Notification)
> **유저 간, 혹은 지원자와 기업 간의 원활한 커뮤니케이션을 위한 실시간 통신 도메인입니다.**

* **실시간 텍스트 채팅**: WebSocket과 STOMP 프로토콜을 활용하여 지연 없는 1:1 채팅을 구현했습니다.
* **다양한 미디어 지원**: 텍스트뿐만 아니라 이미지, 문서 파일 등을 AWS S3와 연동하여 안전하게 첨부하고 공유할 수 있습니다.
* **영상 채팅 (WebRTC)**: 텍스트를 넘어선 깊이 있는 소통과 온라인 면접 등을 위해 1:1 실시간 화상 채팅 기능을 지원합니다.
* **스마트 알림**: 채팅방을 벗어나 있더라도 SSE(Server-Sent Events) 및 WebPush를 통해 즉각적인 알림을 수신할 수 있습니다.

### 💳 7. Orders and Plans (결제 및 구독) [🔗](https://github.com/beyond-sw-camp/be24-3rd-DevOops-Poticard/wiki/Orders-and-Plans)
> **Poticard의 고급 기능을 잠금 해제하는 구독 결제 시스템입니다.**

* **안전한 결제 연동**: PortOne(포트원) API를 통해 신뢰도 높은 결제 시스템을 구축하였으며, 서버와 클라이언트 간의 교차 검증으로 결제 위변조를 차단합니다.
* **PRO 멤버십 혜택**: 결제 완료 시 즉시 AI 첨삭, 프리미엄 명함 테마 등 PRO 요금제 전용 기능에 대한 접근 권한이 부여됩니다.
---

## 🚧 인프라 및 설정 (Infrastructure)

* **AWS S3 연동**: `CloudUploadService`를 통해 프로필 이미지, 채팅 첨부파일, 포트폴리오 에디터 이미지 등을 년/월/일 폴더 구조로 업로드합니다.
* **Exception Handling**: `@RestControllerAdvice`와 커스텀 `BaseException`을 활용하여 성공/실패 응답을 통일된 `BaseResponse` 포맷으로 클라이언트에 전달합니다.
* **로깅 및 모니터링**: `application.yml`을 통해 Actuator 및 Prometheus 설정을 적용하였으며, OpenTelemetry(OTLP)를 통해 Jaeger로 분산 추적 데이터를 전송합니다.
* **설정 파일 분리**: `application.yml`파일을 `application-dev.yml`과 `application-prod.yml`로 분리하여 각 환경에 맞게 적용합니다.
---


<p align="center">Copyright © 2026 DevOops Team. All rights reserved.</p>
