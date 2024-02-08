package app.snob.twitterapiclone.service

import app.snob.twitterapiclone.dto.CommentResponse
import app.snob.twitterapiclone.dto.LikeRequest
import app.snob.twitterapiclone.dto.PostRequest
import app.snob.twitterapiclone.dto.PostResponse
import app.snob.twitterapiclone.model.Comment
import app.snob.twitterapiclone.model.Feed
import app.snob.twitterapiclone.model.Post
import app.snob.twitterapiclone.repository.CommentRepository
import app.snob.twitterapiclone.repository.PostRepository
import app.snob.twitterapiclone.repository.UserRepository
import org.springframework.stereotype.Service

import java.time.LocalDateTime
import java.util.stream.Collectors

@Service
class PostService {
    PostRepository postRepository
    UserRepository userRepository
    CommentRepository commentRepository

    PostService(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository
        this.postRepository = postRepository
        this.commentRepository = commentRepository
    }

    void savePost(PostRequest postRequest) {
        def user = userRepository.findById(postRequest.userId)
                .orElseThrow { new RuntimeException("User not found with ID: $postRequest.userId") }
        def post = Post.builder()
                .content(postRequest.content)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build()
        postRepository.save(post)
    }

    List<PostResponse> getAllPosts() {
        postRepository.findAll().stream()
                .map(this.&mapToPostResponse)
                .collect(Collectors.toList())
    }

    PostResponse getPostById(String id) {
        postRepository.findById(id)
                .map(this.&mapToPostResponse)
                .orElseThrow({ new RuntimeException("Post with ID $id not found") })
    }

    void editPost(String id, PostRequest postUpdateRequest) {
        def post = postRepository.findById(id)
                .orElseThrow { new RuntimeException("Post not found with ID: $id") }
        if (postUpdateRequest.content) {
            post.setContent(postUpdateRequest.content)
        }
        postRepository.save(post)
    }

    void deletePostById(String id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id)
        } else {
            throw new RuntimeException("Post with ID " + id + " not found") as Throwable
        }
    }

    List<PostResponse> getAllPostsByUserSubscribe(String userId) {
        def subscriber = userRepository.findById(userId)
                .orElseThrow { new RuntimeException("User not found with ID: $userId") }

        def subscriptions = subscriber.subscriptions
        def postsFeed = [] as List<PostResponse>
        subscriptions.each { user ->
            postsFeed.addAll(postRepository.findAllByUser(user).stream()
                    .map(this.&mapToPostResponse)
                    .collect(Collectors.toList()))
        }
        postsFeed.sort {it.createdAt}

        return postsFeed
    }

    void addLikeToPost(LikeRequest likeRequest) {
        def post = postRepository.findById(likeRequest.postId)
                .orElseThrow { new RuntimeException("Post not found with ID: $likeRequest.postId") }

        if (!post.getLikedByIds().contains(likeRequest.userId)) {
            post.getLikedByIds().add(likeRequest.userId)

            postRepository.save(post)
        }
    }

    void removeLikeFromPost(LikeRequest likeRequest) {
        def post = postRepository.findById(likeRequest.postId)
                .orElseThrow { new RuntimeException("Post not found with ID: $likeRequest.postId") }

        post.getLikedByIds().remove(likeRequest.userId)

        postRepository.save(post)
    }

    Feed getUserFeed(String userId) {
        def subscriber = userRepository.findById(userId)
                .orElseThrow { new RuntimeException("User not found with ID: $userId") }

        def subscriptions = subscriber.subscriptions

        def subscribedPosts = [] as List<PostResponse>
        def favoritePosts = [] as List<PostResponse>
        def userComments = [] as List<CommentResponse>

        subscriptions.each { user ->
            subscribedPosts.addAll(postRepository.findAllByUser(user).stream()
                    .map(this.&mapToPostResponse)
                    .collect(Collectors.toList()))

            favoritePosts.addAll(subscriber.favoritePosts.stream()
                    .map(this.&mapToPostResponse)
                    .collect(Collectors.toList()))

            userComments.addAll(commentRepository.findAllByUser(user).stream()
                    .map(this.&mapToCommentResponse)
                    .collect(Collectors.toList()))
        }

        def combinedPosts = (subscribedPosts + favoritePosts).unique()

        combinedPosts.sort { it.createdAt }

        return new Feed(combinedPosts, userComments)
    }

    Feed getAnotherUserFeed(String userId) {
        def user = userRepository.findById(userId)
                .orElseThrow { new RuntimeException("User not found with ID: $userId") }

        def favoritePosts = [] as List<PostResponse>
        def userComments = [] as List<CommentResponse>

        favoritePosts.addAll(user.favoritePosts.stream()
                .map(this.&mapToPostResponse)
                .collect(Collectors.toList()))

        userComments.addAll(commentRepository.findAllByUser(user).stream()
                .map(this.&mapToCommentResponse)
                .collect(Collectors.toList()))

        favoritePosts.sort { it.createdAt }

        Feed feed = new Feed(favoritePosts, userComments)

        return feed
    }

    static PostResponse mapToPostResponse(Post post) {
        PostResponse.builder()
                .id(post.id)
                .userId(post.getUser().id)
                .content(post.content)
                .createdAt(post.createdAt)
                .build()
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
