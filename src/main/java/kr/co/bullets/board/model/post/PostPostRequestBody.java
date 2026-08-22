package kr.co.bullets.board.model.post;

public record PostPostRequestBody(String body) {}

//public class PostPostRequestBody {
//
//  private String body;
//
//  public PostPostRequestBody(String body) {
//    this.body = body;
//  }
//
//  // for @RequestBody
//  public PostPostRequestBody() {}
//
//  public String getBody() {
//    return body;
//  }
//
//  public void setBody(String body) {
//    this.body = body;
//  }
//
//  @Override
//  public boolean equals(Object o) {
//    if (!(o instanceof PostPostRequestBody that)) return false;
//    return Objects.equals(getBody(), that.getBody());
//  }
//
//  @Override
//  public int hashCode() {
//    return Objects.hashCode(getBody());
//  }
//}
