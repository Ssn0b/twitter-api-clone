package app.snob.twitterapiclone.service

import app.snob.twitterapiclone.dto.CommentRequest
import app.snob.twitterapiclone.model.Comment
import app.snob.twitterapiclone.repository.CommentRepository
import app.snob.twitterapiclone.repository.PostRepository
import app.snob.twitterapiclone.repository.UserRepository
import org.springframework.stereotype.Service

import java.time.LocalDateTime

@Service
class CommentService {
    CommentRepository commentRepository;
    PostRepository postRepository;
    UserRepository userRepository;

    void commentOnPost(CommentRequest commentRequest) {
        def post = postRepository.findById(commentRequest.postId)
                .orElseThrow { new RuntimeException("Post not found with ID: $commentRequest.postId") }

        def user = userRepository.findById(commentRequest.userId)
                .orElseThrow { new RuntimeException("User not found with ID: $commentRequest.userId") }

        def comment = Comment.builder()
                .user(user)
                .post(post)
                .content(commentRequest.content)
                .createdAt(LocalDateTime.now())
                .build()

        commentRepository.save(comment)
    }

    List<Comment> getPostComments(String postId) {
        def post = postRepository.findById(postId)
                .orElseThrow { new RuntimeException("Post not found with ID: $postId") }
        return commentRepository.findAllByPost(post)
    }
}
