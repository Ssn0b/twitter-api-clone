package app.snob.twitterapiclone.service

import app.snob.twitterapiclone.dto.UserRequest
import app.snob.twitterapiclone.dto.UserResponse
import app.snob.twitterapiclone.model.User
import app.snob.twitterapiclone.repository.PostRepository
import app.snob.twitterapiclone.repository.UserRepository
import org.springframework.stereotype.Service

import java.util.stream.Collectors

@Service
class UserService {
    UserRepository userRepository;
    PostRepository postRepository;

    void saveUser(UserRequest userRequest) {
        def user = User.builder()
                .username(userRequest.username)
                .email(userRequest.email)
                .firstname(userRequest.firstname)
                .lastname(userRequest.lastname)
                .build();
        userRepository.save(user);
    }

    List<UserResponse> getAllUsers() {
        userRepository.findAll().collect(Collectors.toList() as Closure<Object>).stream()
                .map(this.&mapToUserResponse)
                .toList() as List<UserResponse>
    }

    UserResponse getUserById(String id) {
        userRepository.findById(id)
                .map(this.&mapToUserResponse)
                .orElseThrow({ new RuntimeException("User with ID $id not found") })
    }

    void editUser(String id, UserRequest userUpdateRequest) {
        def user = userRepository.findById(id)
                .orElseThrow { new RuntimeException("User not found with ID: $id") }

        if (userUpdateRequest.username) {
            user.setUsername(userUpdateRequest.username)
        }
        if (userUpdateRequest.email) {
            user.setEmail(userUpdateRequest.email)
        }
        if (userUpdateRequest.firstname) {
            user.setFirstname(userUpdateRequest.firstname)
        }
        if (userUpdateRequest.lastname) {
            user.setLastname(userUpdateRequest.lastname)
        }

        userRepository.save(user)
    }

    void deleteUserById(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User with ID " + id + " not found") as Throwable
        }
    }

    void addToFavorite(String userId, String postId) {
        def user = userRepository.findById(userId)
                .orElseThrow { new RuntimeException("User not found with ID: $userId") }
        def post = postRepository.findById(postId)
                .orElseThrow { new RuntimeException("Post not found with ID: $userId") }
        user.getFavoritePosts().add(post)
        userRepository.save(user)
    }

    void deleteFromFavorite(String userId, String postId) {
        def user = userRepository.findById(userId)
                .orElseThrow { new RuntimeException("User not found with ID: $userId") }
        user.getFavoritePosts().removeIf { it.id == postId }
        userRepository.save(user)
    }

    void subscribeToUser(String subscriberId, String userId) {
        def subscriber = userRepository.findById(subscriberId)
                .orElseThrow { new RuntimeException("Subscriber not found with ID: $subscriberId") }

        def userToSubscribe = userRepository.findById(userId)
                .orElseThrow { new RuntimeException("User to subscribe not found with ID: $userId") }

        subscriber.getSubscriptions().add(userToSubscribe)
        userRepository.save(subscriber)
    }

    void unsubscribeFromUser(String subscriberId, String userId) {
        def subscriber = userRepository.findById(subscriberId)
                .orElseThrow { new RuntimeException("Subscriber not found with ID: $subscriberId") }

        subscriber.getSubscriptions().removeIf { it.id == userId }
        userRepository.save(subscriber)
    }

    static UserResponse mapToUserResponse(User user) {
        UserResponse.builder()
                .id(user.id)
                .username(user.username)
                .email(user.email)
                .firstname(user.firstname)
                .lastname(user.lastname)
                .favoritePosts(user.favoritePosts)
                .subscriptions(user.subscriptions)
                .build()
    }
}
