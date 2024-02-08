package app.snob.twitterapiclone.service

import app.snob.twitterapiclone.dto.CommentRequest
import app.snob.twitterapiclone.dto.CommentResponse
import app.snob.twitterapiclone.model.Comment
import app.snob.twitterapiclone.repository.CommentRepository
import app.snob.twitterapiclone.repository.PostRepository
import app.snob.twitterapiclone.repository.UserRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

import java.time.LocalDateTime
import java.util.stream.Collectors

@Service
class CommentService {
    CommentRepository commentRepository
    PostRepository postRepository
    UserRepository userRepository

    CommentService(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository
        this.postRepository = postRepository
        this.commentRepository = commentRepository
    }

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

    List<CommentResponse> getPostComments(String postId) {
        def post = postRepository.findById(postId)
                .orElseThrow { new RuntimeException("Post not found with ID: $postId") }
        return commentRepository.findAllByPost(post).stream()
                .map(this.&mapToCommentResponse)
                .collect(Collectors.toList())
    }

    static CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse.builder()
                .id(comment.id)
                .userId(comment.user.id)
                .postId(comment.post.id)
                .content(comment.content)
                .createdAt(comment.createdAt)
                .build()
    }
}
