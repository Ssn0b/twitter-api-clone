package app.snob.twitterapiclone.service

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

import java.util.stream.Collectors

@Service
class PostService {
    PostRepository postRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;

    void savePost(PostRequest postRequest) {
        def user = userRepository.findById(postRequest.userId)
                .orElseThrow { new RuntimeException("User not found with ID: $postRequest.userId") }
        def post = Post.builder()
                .content(postRequest.content)
                .createdAt(postRequest.createdAt)
                .user(user)
                .build();
        postRepository.save(post);
    }

    List<PostResponse> getAllPosts() {
        postRepository.findAll().collect(Collectors.toList() as Closure<Object>).stream()
                .map(this.&mapToPostResponse)
                .toList() as List<PostResponse>
    }

    PostResponse getPostById(String id) {
        postRepository.findById(id)
                .map(this.&mapToPostResponse)
                .orElseThrow({ new RuntimeException("Post with ID $id not found") })
    }

    void editPost(String id, PostRequest postUpdateRequest) {
        def post = postRepository.findById(id)
                .orElseThrow { new RuntimeException("Post not found with ID: $id") }
        if (postUpdateRequest.createdAt) {
            post.setCreatedAt(postUpdateRequest.createdAt)
        }
        if (postUpdateRequest.content) {
            post.setContent(postUpdateRequest.content)
        }
        postRepository.save(post)
    }

    void deletePostById(String id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
        } else {
            throw new RuntimeException("Post with ID " + id + " not found") as Throwable
        }
    }

    List<Post> getAllPostsByUserSubscribe(String userId) {
        def subscriber = userRepository.findById(userId)
                .orElseThrow { new RuntimeException("User not found with ID: $userId") }

        def subscriptions = subscriber.subscriptions
        def postsFeed = [] as List<Post>
        subscriptions.each { user ->
            postsFeed.addAll(postRepository.findAllByUser(user))
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

        def subscribedPosts = [] as List<Post>
        def likedPosts = [] as List<Post>
        def userComments = [] as List<Comment>

        subscriptions.each { user ->
            subscribedPosts.addAll(postRepository.findAllByUser(user))

            likedPosts.addAll(subscriber.favoritePosts)

            userComments.addAll(commentRepository.findAllByUser(user))
        }

        def combinedPosts = (subscribedPosts + likedPosts).unique()

        combinedPosts.sort { it.createdAt }

        return new Feed(combinedPosts, userComments)
    }

    Feed getAnotherUserFeed(String userId) {
        def user = userRepository.findById(userId)
                .orElseThrow { new RuntimeException("User not found with ID: $userId") }

        def likedPosts = user.favoritePosts
        def userComments = commentRepository.findAllByUser(user)

        likedPosts.sort { it.createdAt }

        return new Feed(likedPosts, userComments)
    }

    static PostResponse mapToPostResponse(Post post) {
        PostResponse.builder()
                .id(post.id)
                .user(post.getUser())
                .content(post.content)
                .createdAt(post.createdAt)
                .build()
    }
}
