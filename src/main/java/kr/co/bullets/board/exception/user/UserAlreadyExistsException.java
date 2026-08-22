package kr.co.bullets.board.exception.user;

import kr.co.bullets.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ClientErrorException {

  public UserAlreadyExistsException() {
    super(HttpStatus.CONFLICT, "User already exists.");
  }

  public UserAlreadyExistsException(String username) {
    super(HttpStatus.CONFLICT, "User with username " + username + " already exists.");
  }
}
