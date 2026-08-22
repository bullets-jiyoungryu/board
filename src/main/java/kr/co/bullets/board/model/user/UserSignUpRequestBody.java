package kr.co.bullets.board.model.user;

import jakarta.validation.constraints.NotEmpty;

public record UserSignUpRequestBody(@NotEmpty String username, @NotEmpty String password) {}
