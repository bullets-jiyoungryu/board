package kr.co.bullets.board.controller;

import jakarta.validation.Valid;
import kr.co.bullets.board.model.user.User;
import kr.co.bullets.board.model.user.UserSignUpRequestBody;
import kr.co.bullets.board.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  @Autowired UserService userService;

  @PostMapping
  public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequestBody userSignUpRequestBody) {
    var user =
        userService.signUp(userSignUpRequestBody.username(), userSignUpRequestBody.password());
    return ResponseEntity.ok(user);
//    return new ResponseEntity<>(user, HttpStatus.OK);
  }
}
