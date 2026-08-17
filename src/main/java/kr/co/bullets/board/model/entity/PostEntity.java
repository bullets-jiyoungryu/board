package kr.co.bullets.board.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "post")
// 소프트 삭제(soft delete) — 이 엔티티를 delete 할 때 실행할 SQL 을 직접 지정한다.
// 기본 동작인 `DELETE FROM post WHERE postid=?` 대신 아래 UPDATE 가 나가므로,
// 행은 그대로 남고 deleteddatetime 에 삭제 시각만 기록된다.
// 끝의 `?` 에는 @Id 필드(postId) 값이 바인딩된다 — 파라미터 순서/개수를 마음대로 바꾸면 안 된다.
// 컬럼명을 소문자로 쓴 이유: 이 문자열은 JPQL 이 아니라 네이티브 SQL 이라 필드명(deletedDateTime)이 아닌
// 실제 컬럼명을 써야 한다. 네이밍 전략이 Standard(camelCase 유지)라 DDL 은 deletedDateTime 으로 나가지만,
// PostgreSQL 은 따옴표 없는 식별자를 소문자로 접으므로 실제 컬럼명은 deleteddatetime 이 된다.
// 주의: 이 UPDATE 는 DB 만 바꾼다. 메모리상 엔티티의 deletedDateTime 필드는 갱신되지 않고 @PreUpdate 도 타지 않는다.
@SQLDelete(sql = "UPDATE \"post\" SET deleteddatetime = CURRENT_TIMESTAMP WHERE postid = ?")
// Deprecated in Hibernate 6.3
// @Where(clause = "deletedDateTime IS NULL")
// 이 엔티티를 읽는 SQL 의 WHERE 에 자동으로 덧붙는 조건. 위에서 소프트 삭제된 행을 조회 결과에서 걸러낸다.
// 덕분에 findAll() 이 `... where deleteddatetime is null` 로 나가 살아있는 게시물만 돌려준다.
// @SQLDelete 와 한 쌍이다 — 이게 없으면 삭제한 글이 목록에 계속 보인다.
// 주의: JPQL/HQL/Criteria 와 연관 컬렉션 로딩에는 적용되지만, PK 직접 조회(EntityManager.find, 즉 findById)와
// 네이티브 쿼리에는 적용되지 않는 것으로 알려져 있다. 삭제된 글이 findById 로는 잡힐 수 있다.
@SQLRestriction("deleteddatetime IS NULL")
public class PostEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  @Column(columnDefinition = "TEXT")
  private String body;

  @Column private ZonedDateTime createdDateTime;

  @Column private ZonedDateTime updatedDateTime;

  @Column private ZonedDateTime deletedDateTime;

  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public ZonedDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public void setCreatedDateTime(ZonedDateTime createdDateTime) {
    this.createdDateTime = createdDateTime;
  }

  public ZonedDateTime getUpdatedDateTime() {
    return updatedDateTime;
  }

  public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
    this.updatedDateTime = updatedDateTime;
  }

  public ZonedDateTime getDeletedDateTime() {
    return deletedDateTime;
  }

  public void setDeletedDateTime(ZonedDateTime deletedDateTime) {
    this.deletedDateTime = deletedDateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof PostEntity that)) return false;
    return Objects.equals(getPostId(), that.getPostId())
        && Objects.equals(getBody(), that.getBody())
        && Objects.equals(getCreatedDateTime(), that.getCreatedDateTime())
        && Objects.equals(getUpdatedDateTime(), that.getUpdatedDateTime())
        && Objects.equals(getDeletedDateTime(), that.getDeletedDateTime());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getPostId(), getBody(), getCreatedDateTime(), getUpdatedDateTime(), getDeletedDateTime());
  }

  @PrePersist
  private void prePersist() {
    this.createdDateTime = ZonedDateTime.now();
    this.updatedDateTime = this.createdDateTime;
  }

  @PreUpdate
  private void preUpdate() {
    this.updatedDateTime = ZonedDateTime.now();
  }
}
