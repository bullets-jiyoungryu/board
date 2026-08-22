package kr.co.bullets.board.exception.user;

import kr.co.bullets.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserNotAllowedException extends ClientErrorException {

  public UserNotAllowedException() {
    super(HttpStatus.FORBIDDEN, "User not allowed.");
  }
}
