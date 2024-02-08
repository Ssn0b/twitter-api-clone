package app.snob.twitterapiclone.service

import app.snob.twitterapiclone.dto.PostRequest
import app.snob.twitterapiclone.dto.PostResponse
import app.snob.twitterapiclone.dto.UserRequest
import app.snob.twitterapiclone.dto.UserResponse
import app.snob.twitterapiclone.model.Post
import app.snob.twitterapiclone.repository.PostRepository
import app.snob.twitterapiclone.repository.UserRepository
import org.springframework.stereotype.Service

import java.util.stream.Collectors

@Service
class PostService {
    PostRepository postRepository;
    UserRepository userRepository;

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

        // Initialize an empty list to store the subscriber's feed
        def userFeed = subscriber.subscriptions

        // Iterate through the subscriber's subscriptions
        subscriber.subscriptions.each { subscribedUser ->
            // Retrieve posts from the subscribed user
            def postsFromSubscribedUser = subscribedUser.posts

            // Add the posts to the subscriber's feed
            userFeed.addAll(postsFromSubscribedUser)
        }

        return userFeed
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
