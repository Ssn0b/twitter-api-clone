package app.snob.twitterapiclone.service

import app.snob.twitterapiclone.dto.UserRequest
import app.snob.twitterapiclone.dto.UserResponse
import app.snob.twitterapiclone.model.Post
import app.snob.twitterapiclone.model.User
import app.snob.twitterapiclone.repository.PostRepository
import app.snob.twitterapiclone.repository.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

class UserServiceSpec extends Specification {
    UserRepository userRepository
    PostRepository postRepository
    UserService userService

    def setup() {
        userRepository = Mock()
        postRepository = Mock()
        userService = new UserService(userRepository, postRepository)
    }

    @Unroll
    def "saveUser should save a new user"() {
        given:
        def userRequest = new UserRequest(username: "testUser", email: "test@example.com",
                firstname: "Test", lastname: "User")

        when:
        userService.saveUser(userRequest)

        then:
        1 * userRepository.save(_)
    }

    @Unroll
    def "getAllUsers should return a list of all users"() {
        given:
        userRepository.findAll() >> [new User(username: "testUser1"), new User(username: "testUser2")]

        when:
        List<UserResponse> allUsers = userService.getAllUsers()

        then:
        allUsers.size() == 2
        allUsers*.username == ["testUser1", "testUser2"]
    }

    @Unroll
    def "getUserById should return the user with the specified ID"() {
        given:
        def userId = "123456"
        def user = new User(id: userId, username: "testUser", email: "test@example.com",
                firstname: "Test", lastname: "User")
        userRepository.findById(userId) >> Optional.of(user)

        when:
        UserResponse userResponse = userService.getUserById(userId)

        then:
        userResponse.username == "testUser"
        userResponse.id == userId
    }

    def "editUser should update user information"() {
        given:
        def userId = "123"
        def userRequest = new UserRequest(username: "newUsername", email: "newemail@example.com", firstname: "NewFirst", lastname: "NewLast")
        def user = new User(id: userId, username: "oldUsername", email: "oldemail@example.com", firstname: "OldFirst", lastname: "OldLast")
        userRepository.findById(userId) >> Optional.of(user)

        when:
        userService.editUser(userId, userRequest)

        then:
        1 * userRepository.save(_)
        user.username == "newUsername"
        user.email == "newemail@example.com"
        user.firstname == "NewFirst"
        user.lastname == "NewLast"
    }

    def "deleteUserById should delete user by ID"() {
        given:
        def userId = "123"
        userRepository.existsById(userId) >> true

        when:
        userService.deleteUserById(userId)

        then:
        1 * userRepository.deleteById(userId)
    }

    def "addToFavorite should add post to user's favorite posts"() {
        given:
        def userId = "123"
        def postId = "456"
        def user = new User(id: userId)
        def post = new Post(id: postId)
        userRepository.findById(userId) >> Optional.of(user)
        postRepository.findById(postId) >> Optional.of(post)

        when:
        userService.addToFavorite(userId, postId)

        then:
        1 * userRepository.save(_)
        user.favoritePosts.contains(post)
    }

    def "deleteFromFavorite should remove post from user's favorite posts"() {
        given:
        def userId = "123"
        def postId = "456"
        def user = new User(id: userId, favoritePosts: [new Post(id: postId)])
        userRepository.findById(userId) >> Optional.of(user)

        when:
        userService.deleteFromFavorite(userId, postId)

        then:
        1 * userRepository.save(_)
        !user.favoritePosts.any { it.id == postId }
    }

    def "subscribeToUser should add user to subscriber's subscriptions"() {
        given:
        def subscriberId = "subscriberId"
        def userId = "userId"
        def subscriber = new User(id: subscriberId)
        def userToSubscribe = new User(id: userId)
        userRepository.findById(subscriberId) >> Optional.of(subscriber)
        userRepository.findById(userId) >> Optional.of(userToSubscribe)

        when:
        userService.subscribeToUser(subscriberId, userId)

        then:
        1 * userRepository.save(_)
        subscriber.subscriptions.contains(userToSubscribe)
    }

    def "unsubscribeFromUser should remove user from subscriber's subscriptions"() {
        given:
        def subscriberId = "subscriberId"
        def userId = "userId"
        def subscriber = new User(id: subscriberId, subscriptions: [new User(id: userId)])
        userRepository.findById(subscriberId) >> Optional.of(subscriber)

        when:
        userService.unsubscribeFromUser(subscriberId, userId)

        then:
        1 * userRepository.save(_)
        !subscriber.subscriptions.any { it.id == userId }
    }
}
