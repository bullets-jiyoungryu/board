package kr.co.bullets.board.controller;

import kr.co.bullets.board.model.Post;
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
    //    List<Post> posts = new ArrayList<>();
    //    posts.add(new Post(1L, "Post 1", ZonedDateTime.now()));
    //    posts.add(new Post(2L, "Post 2", ZonedDateTime.now()));
    //    posts.add(new Post(3L, "Post 3", ZonedDateTime.now()));

    List<Post> posts = postService.getPosts();

    //    return new ResponseEntity<>(posts, HttpStatus.OK);

    return ResponseEntity.ok(posts);
  }

  @GetMapping("/{postId}")
  public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
    //    List<Post> posts = new ArrayList<>();
    //    posts.add(new Post(1L, "Post 1", ZonedDateTime.now()));
    //    posts.add(new Post(2L, "Post 2", ZonedDateTime.now()));
    //    posts.add(new Post(3L, "Post 3", ZonedDateTime.now()));

    //    Optional<Post> matchingPost =
    //        posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();

    Optional<Post> matchingPost = postService.getPostByPostId(postId);

    return matchingPost
        .map(ResponseEntity::ok)
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  // POST /posts
  @PostMapping
  public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody postPostRequestBody) {
//    Post post = postService.createPost(postPostRequestBody);
    var post = postService.createPost(postPostRequestBody);
    return ResponseEntity.ok(post);
  }
}
