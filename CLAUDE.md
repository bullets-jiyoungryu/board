# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

`board` — Spring Boot 4.1.0 / Java 25 기반 REST API 프로젝트. group `kr.co.bullets`, 베이스 패키지 `kr.co.bullets.board`.

아직 초기 단계다. 자바 파일은 4개뿐이다.

- `kr.co.bullets.board.BoardApplication` — `@SpringBootApplication` 진입점
- `kr.co.bullets.board.controller.PostController` — GET 엔드포인트 2개. 필드·생성자 없이 메서드 본문에서 하드코딩 목 데이터를 만들어 반환한다
- `kr.co.bullets.board.model.Post` — Lombok 없이 손으로 쓴 가변 POJO. 3-arg 생성자 + getter/setter, `equals`/`hashCode`/`toString` 수동 구현. record가 아니며 시간 타입은 `ZonedDateTime`
- `kr.co.bullets.board.BoardApplicationTests` — `@SpringBootTest` + `contextLoads()` 스모크 테스트

service / repository 패키지는 아직 없다. 도메인 타입은 `entity`/`dto`가 아니라 **`model` 패키지**에 두는 것이 현재 유일한 선례다.

## API

| 메서드 | 경로 | 반환 |
| --- | --- | --- |
| GET | `/api/v1/posts` | `ResponseEntity<List<Post>>` (200) |
| GET | `/api/v1/posts/{postId}` | `ResponseEntity<Post>` (200 / 404) |

- 경로 프리픽스는 `/api/v1/...`. 클래스 레벨 `@RequestMapping` 없이 **메서드마다 전체 경로**를 적는다 (`@GetMapping("/api/v1/posts")`).
- 응답은 `ResponseEntity<T>`를 직접 반환한다. 공통 응답 래퍼도 `@ControllerAdvice`도 없다. 200은 `ResponseEntity::ok` 또는 `new ResponseEntity<>(body, HttpStatus.OK)`, 404는 `new ResponseEntity<>(HttpStatus.NOT_FOUND)`.
- **데이터는 전부 메서드 안에서 만드는 목이다.** 두 메서드에 같은 `List<Post>` 생성 코드가 중복돼 있고, 실제 저장소는 존재하지 않는다 — 영속성 계층이 붙기 전까지 이 목 데이터를 데이터 소스로 취급하지 말 것.

## 명령어

```bash
./gradlew build     # 컴파일 + 테스트
./gradlew bootRun   # 애플리케이션 실행
./gradlew test      # 전체 테스트

# 단일 테스트 클래스
./gradlew test --tests "kr.co.bullets.board.BoardApplicationTests"

# 단일 테스트 메서드
./gradlew test --tests "kr.co.bullets.board.BoardApplicationTests.contextLoads"
```

lint / formatter 플러그인은 설정되어 있지 않다 (Checkstyle, Spotless 등 없음).

## 빌드 · 툴체인 제약

- 빌드 스크립트는 **Kotlin DSL** (`build.gradle.kts`). Groovy DSL 문법을 쓰지 말 것.
- Gradle wrapper **9.5.1**.
- Java toolchain **25** (`JavaLanguageVersion.of(25)`).
- **Spring Boot 4.1.0** + `io.spring.dependency-management` 1.1.7 → 의존성은 버전 없이 선언한다.
- `tasks.withType<Test> { useJUnitPlatform() }` — JUnit 5.

### Spring Boot 4 스타터 이름 주의

Boot 4에서 웹 스타터 아티팩트명이 바뀌었다. 현재 선언된 의존성은 아래 3개가 전부다.

```kotlin
implementation("org.springframework.boot:spring-boot-starter-webmvc")
testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
testRuntimeOnly("org.junit.platform:junit-platform-launcher")
```

Boot 3 시절의 `spring-boot-starter-web` / `spring-boot-starter-test`를 관성으로 쓰면 안 된다. 새 스타터를 추가할 때도 Boot 4 기준 모듈명을 먼저 확인할 것.

## 현재 클래스패스에 없는 것

아래는 모두 미도입 상태라, 사용하려면 먼저 `build.gradle.kts`에 의존성을 추가해야 한다.

- **Lombok** — `@Getter`, `@RequiredArgsConstructor` 등 사용 불가 (애노테이션 프로세서 미설정). 그래서 `model/Post.java`의 getter/setter/`equals`/`hashCode`/`toString`이 전부 수작업이다
- **Bean Validation** — `jakarta.validation`, `@Valid` 사용 불가
- **영속성 계층 전체** — JPA / MyBatis / JDBC 모두 없음. 데이터소스 설정도 없음. 어떤 스택을 쓸지는 아직 결정되지 않은 열린 선택지다.
- **뷰 템플릿** — Thymeleaf, JSP 없음. `src/main/resources/templates/`와 `static/` 디렉터리는 있으나 비어 있다.

## 구조 · 컨벤션

- 패키지는 **계층(기술) 기반**이다: `kr.co.bullets.board.controller`, `kr.co.bullets.board.model`. 기능 기반(`...board.post.*`)이 아니다.
- 설정 파일은 `src/main/resources/application.yaml` (`.yml` 아님). 현재 내용은 `spring.application.name: board` 3줄이 전부이고, 프로파일별 설정 파일은 없다.
- Java 들여쓰기는 **스페이스 2칸** (google-java-format 기준). 단, Initializr 생성 파일(`BoardApplication`, `BoardApplicationTests`, `build.gradle.kts`)과 `model/Post.java`는 아직 정리되지 않아 탭 / 스페이스 4가 섞여 있다.
- 테스트는 JUnit 5 + `@SpringBootTest`. `src/test/resources/` 디렉터리는 없다. 컨트롤러 슬라이스 테스트(`@WebMvcTest`)는 아직 없다.
- 응답 스타일은 위 `## API`의 `ResponseEntity` 직접 반환이 선례다. 반면 **예외 처리(`@ControllerAdvice`), DTO 분리(현재는 `model`을 그대로 응답 바디로 노출), 의존성 주입 스타일, 영속성 스택**은 여전히 선례가 없다 — 새로 도입할 때 사용자와 방향을 맞출 것.

## Git

- 원격은 `github.com/bullets-jiyoungryu/board`, 기본 브랜치 `main`. 브랜치를 따로 파지 않고 `main`에 직접 커밋하는 방식으로 작업 중이다.
- 커밋 메시지는 **Conventional Commits v1.0.0**을 따른다 (https://www.conventionalcommits.org/en/v1.0.0/). 예: `feat(post): 게시물을 위한 CRUD API(실습) - READ`
- `HELP.md`(Initializr 생성 문서)는 `.gitignore`에 등재되어 추적되지 않는다.
