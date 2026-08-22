package kr.co.bullets.board.service;

import kr.co.bullets.board.exception.post.PostNotFoundException;
import kr.co.bullets.board.exception.user.UserNotAllowedException;
import kr.co.bullets.board.model.entity.UserEntity;
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
        postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
    return Post.from(postEntity);
  }

  public Post createPost(PostPostRequestBody postPostRequestBody, UserEntity currentUser) {
    var savedPostEntity =
        postEntityRepository.save(PostEntity.of(postPostRequestBody.body(), currentUser));
    return Post.from(savedPostEntity);
  }

  public Post updatePost(
      Long postId, PostPatchRequestBody postPatchRequestBody, UserEntity currentUser) {
    var postEntity =
        postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    if (!postEntity.getUser().equals(currentUser)) {
      throw new UserNotAllowedException();
    }

    postEntity.setBody(postPatchRequestBody.body());
    var updatedEntity = postEntityRepository.save(postEntity);
    return Post.from(updatedEntity);
  }

  public void deletePost(Long postId, UserEntity currentUser) {
    var postEntity =
        postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    if (!postEntity.getUser().equals(currentUser)) {
      throw new UserNotAllowedException();
    }

    postEntityRepository.delete(postEntity);
  }
}
