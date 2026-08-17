package kr.co.bullets.board.controller;

import kr.co.bullets.board.model.Post;
import kr.co.bullets.board.model.PostPatchRequestBody;
import kr.co.bullets.board.model.PostPostRequestBody;
import kr.co.bullets.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

  @Autowired private PostService postService;

  @GetMapping
  public ResponseEntity<List<Post>> getPosts() {
    var posts = postService.getPosts();
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/{postId}")
  public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
    var post = postService.getPostByPostId(postId);
    return ResponseEntity.ok(post);
  }

  // POST /posts
  @PostMapping
  public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody postPostRequestBody) {
    //    Post post = postService.createPost(postPostRequestBody);
    var post = postService.createPost(postPostRequestBody);
    return ResponseEntity.ok(post);
  }

  @PatchMapping("/{postId}")
  public ResponseEntity<Post> updatePost(
      @PathVariable Long postId, @RequestBody PostPatchRequestBody postPatchRequestBody) {
    var post = postService.updatePost(postId, postPatchRequestBody);
    return ResponseEntity.ok(post);
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
    postService.deletePost(postId);
    return ResponseEntity.noContent().build();
  }
}
