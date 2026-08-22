package kr.co.bullets.board.service;

import kr.co.bullets.board.exception.post.PostNotFoundException;
import kr.co.bullets.board.model.post.Post;
import kr.co.bullets.board.model.post.PostPatchRequestBody;
import kr.co.bullets.board.model.post.PostPostRequestBody;
import kr.co.bullets.board.model.entity.PostEntity;
import kr.co.bullets.board.repository.PostEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

  @Autowired private PostEntityRepository postEntityRepository;

  public List<Post> getPosts() {
    var postEntities = postEntityRepository.findAll();
    return postEntities.stream().map(Post::from).toList();
  }

  public Post getPostByPostId(Long postId) {
    var postEntity =
        postEntityRepository
            .findById(postId)
            .orElseThrow(
                () -> new PostNotFoundException(postId));
    return Post.from(postEntity);
  }

  public Post createPost(PostPostRequestBody postPostRequestBody) {
    var postEntity = new PostEntity();
    postEntity.setBody(postPostRequestBody.body());
    var savedPostEntity = postEntityRepository.save(postEntity);
    return Post.from(savedPostEntity);
  }

  public Post updatePost(Long postId, PostPatchRequestBody postPatchRequestBody) {
    var postEntity =
        postEntityRepository
            .findById(postId)
            .orElseThrow(
                () -> new PostNotFoundException(postId));
    postEntity.setBody(postPatchRequestBody.body());
    var updatedPostEntity = postEntityRepository.save(postEntity);
    return Post.from(updatedPostEntity);
  }

  public void deletePost(Long postId) {
    var postEntity =
        postEntityRepository
            .findById(postId)
            .orElseThrow(
                () -> new PostNotFoundException(postId));
    postEntityRepository.delete(postEntity);
  }
}
