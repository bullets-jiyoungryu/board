package kr.co.bullets.board.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.bullets.board.model.entity.PostEntity;

import java.time.ZonedDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post(
    Long postId,
    String body,
    ZonedDateTime createdDateTime,
    ZonedDateTime updatedDateTime,
    ZonedDateTime deletedDateTime) {

  public static Post from(PostEntity postEntity) {
    return new Post(
        postEntity.getPostId(),
        postEntity.getBody(),
        postEntity.getCreatedDateTime(),
        postEntity.getUpdatedDateTime(),
        postEntity.getDeletedDateTime());
  }
}
