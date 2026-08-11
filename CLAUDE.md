# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

`board` — Spring Boot 4.1.0 / Java 25 기반 REST API 프로젝트. group `kr.co.bullets`, 베이스 패키지 `kr.co.bullets.board`.

게시물(Post) CRUD를 인메모리로 구현한 **학습용 실습 프로젝트**다. 자바 파일은 6개 + 테스트 1개.

- `BoardApplication` — `@SpringBootApplication` 진입점
- `controller/PostController` — CRUD 엔드포인트 5개. 로직 없이 `PostService`에 위임만 한다
- `service/PostService` — `static final List<Post>` 인메모리 저장소 + 비즈니스 로직 전부
- `model/Post` — Lombok 없이 손으로 쓴 가변 POJO. 3-arg 생성자 + getter/setter, `equals`/`hashCode`/`toString` 수동 구현. record가 아니며 시간 타입은 `ZonedDateTime`
- `model/PostPostRequestBody`, `model/PostPatchRequestBody` — 요청 바디 전용 `record ...(String body)`
- `BoardApplicationTests` — `@SpringBootTest` + `contextLoads()` 스모크 테스트

repository 패키지는 없다. 도메인 타입도 요청 DTO도 `entity`/`dto`가 아니라 **`model` 패키지** 한 곳에 둔다.

## 실습 코드 특성 — 함부로 정리하지 말 것

강의를 따라가며 쓴 코드라 **이전 단계의 구현이 주석으로 통째 남아 있다.** 학습 기록이므로 리팩터링·클린업 대상이 아니다. 사용자가 명시적으로 요청할 때만 손댄다.

- `PostController`/`PostService`의 `//    List<Post> posts = new ArrayList<>();` 류 주석 블록 — 목 데이터 시절 코드
- `model/Post.java` 상단의 `//public record Post(...)`, 서비스의 `//    ... post.postId()` — record 전환 검토 흔적
- `PostPatchRequestBody.java`의 주석 블록은 `PostPostRequestBody`를 복사한 것이라 클래스명이 맞지 않고, `PostPostRequestBody.java`의 `import java.util.Objects;`는 주석 안에서만 쓰여 미사용이다 — 둘 다 알려진 상태이며 그대로 둔다

## API

| 메서드 | 경로 | 반환 |
| --- | --- | --- |
| GET | `/api/v1/posts` | 200 `List<Post>` |
| GET | `/api/v1/posts/{postId}` | 200 `Post` / 404 (빈 바디) |
| POST | `/api/v1/posts` | 200 `Post` (201 아님) |
| PATCH | `/api/v1/posts/{postId}` | 200 `Post` / 404 |
| DELETE | `/api/v1/posts/{postId}` | 204 / 404 |

- 라우팅은 클래스 레벨 `@RequestMapping("/api/v1/posts")` + 메서드에는 나머지 경로만 (`@GetMapping`, `@GetMapping("/{postId}")`). 메서드에 전체 경로를 적던 초기 방식은 CREATE 단계에서 폐기됐다.
- 응답은 `ResponseEntity<T>`를 직접 반환한다. 공통 응답 래퍼도 `@ControllerAdvice`도 없다.
- **404 처리가 두 방식으로 혼재한다.** 단건 조회는 컨트롤러에서 `Optional` + `new ResponseEntity<>(HttpStatus.NOT_FOUND)`, update/delete는 서비스에서 `throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.")`. 새 엔드포인트는 후자(서비스에서 던지기)가 최신 선례다.
- **데이터는 `PostService`의 `static` 리스트다.** 애플리케이션 재시작 시 `Post 1~3`으로 리셋되고, 동시성 보호가 없다(`ArrayList`). `getPosts()`는 내부 리스트를 방어 복사 없이 그대로 반환한다.
- 새 `postId`는 `posts.stream().mapToLong(Post::getPostId).max().orElse(0L) + 1`로 계산한다 — 시퀀스가 아니라 최대값 +1이다.

## 명령어

```bash
./gradlew build          # 컴파일 + 테스트
./gradlew clean build    # 커밋 전 검증용 (IDE가 먼저 컴파일하면 build가 UP-TO-DATE로 넘어가는 경우가 있다)
./gradlew bootRun        # 애플리케이션 실행
./gradlew test           # 전체 테스트

# 단일 테스트 클래스
./gradlew test --tests "kr.co.bullets.board.BoardApplicationTests"

# 단일 테스트 메서드
./gradlew test --tests "kr.co.bullets.board.BoardApplicationTests.contextLoads"
```

수동 확인용 (`bootRun` 실행 상태에서):

```bash
curl localhost:8080/api/v1/posts
curl -X POST localhost:8080/api/v1/posts -H 'Content-Type: application/json' -d '{"body":"Post 4"}'
curl -X PATCH localhost:8080/api/v1/posts/4 -H 'Content-Type: application/json' -d '{"body":"수정"}'
curl -i -X DELETE localhost:8080/api/v1/posts/4
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
- **Bean Validation** — `jakarta.validation`, `@Valid` 사용 불가. 그래서 요청 바디 검증이 전혀 없다(`body`가 null이어도 통과)
- **영속성 계층 전체** — JPA / MyBatis / JDBC 모두 없음. 데이터소스 설정도 없음. `PostService`의 `static List`가 임시 저장소이며, 어떤 스택으로 대체할지는 아직 열린 선택지다
- **뷰 템플릿** — Thymeleaf, JSP 없음. `src/main/resources/templates/`와 `static/` 디렉터리는 있으나 비어 있다

## 구조 · 컨벤션

- 패키지는 **계층(기술) 기반**이다: `controller`, `service`, `model`. 기능 기반(`...board.post.*`)이 아니다.
- DI는 **`@Autowired` 필드 주입** (`@Autowired private PostService postService;`). 생성자 주입 선례는 아직 없다.
- 지역 변수에 `var`를 즐겨 쓴다 (`var post = postService.createPost(...)`).
- import는 와일드카드가 섞여 있다 (`org.springframework.web.bind.annotation.*`) — IDE 자동 정리 결과이므로 굳이 펼치지 않는다.
- 설정 파일은 `src/main/resources/application.yaml` (`.yml` 아님). 현재 내용은 `spring.application.name: board` 3줄이 전부이고, 프로파일별 설정 파일은 없다.
- Java 들여쓰기는 **스페이스 2칸** (google-java-format 기준). Initializr 생성 파일(`BoardApplication`, `BoardApplicationTests`, `build.gradle.kts`)과 일부 최근 코드(긴 메서드 시그니처, 주석 블록)는 포맷이 적용되지 않은 상태다.
- 테스트는 JUnit 5 + `@SpringBootTest`. `src/test/resources/` 디렉터리는 없다. 컨트롤러 슬라이스 테스트(`@WebMvcTest`)나 CRUD 동작 테스트는 아직 없다 — 검증은 `curl` 수동 확인으로 하고 있다.
- **선례가 아직 없는 것**: `@ControllerAdvice` 전역 예외 처리, 응답 DTO 분리(현재는 `model/Post`를 그대로 응답 바디로 노출), 영속성 스택, 페이징. 새로 도입할 때 사용자와 방향을 맞출 것.

## Git

- 원격은 `github.com/bullets-jiyoungryu/board`, 기본 브랜치 `main`. 브랜치를 따로 파지 않고 `main`에 직접 커밋하는 방식으로 작업 중이다.
- 커밋 메시지는 **Conventional Commits v1.0.0**을 따른다 (https://www.conventionalcommits.org/en/v1.0.0/). 본문은 대체로 생략하고 **제목 한 줄**로 끝낸다.
- 단, Claude가 만든 커밋에는 **`Co-Authored-By: Claude Opus 5 (1M context) <noreply@anthropic.com>` 트레일러를 반드시 붙인다** (제목 뒤 빈 줄 한 칸). GitHub 커밋 목록의 "and claude committed" 표시가 이 트레일러에서 나온다 — "제목 한 줄" 관례를 이유로 생략하지 말 것.
- 실습 단계는 한 단계당 한 커밋으로 남긴다: `feat(post): 게시물을 위한 CRUD API(실습) - READ` / `- CREATE` / `- UPDATE` / `- DELETE`.
- 사용자가 코드를 직접 작성한 뒤 커밋·푸시만 맡기는 흐름이 많다. 이때는 **소스를 손대지 말고** `./gradlew clean build`로 검증한 뒤 `git add` → 커밋 → `git push origin main`까지 한다.
- `HELP.md`(Initializr 생성 문서)는 `.gitignore`에 등재되어 추적되지 않는다.
