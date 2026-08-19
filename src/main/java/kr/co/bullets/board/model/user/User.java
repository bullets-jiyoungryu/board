package kr.co.bullets.board.model.user;

import java.time.ZonedDateTime;
import kr.co.bullets.board.model.entity.UserEntity;

public record User(
    Long userId,
    String username,
    String profile,
    String description,
    ZonedDateTime createdDateTime,
    ZonedDateTime updatedDateTime) {

  public static User from(UserEntity user) {
    return new User(
        user.getUserId(),
        user.getUsername(),
        user.getProfile(),
        user.getDescription(),
        user.getCreatedDateTime(),
        user.getUpdatedDateTime());
  }
}
