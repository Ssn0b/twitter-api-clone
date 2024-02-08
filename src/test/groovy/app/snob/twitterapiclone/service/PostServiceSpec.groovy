package app.snob.twitterapiclone.service

import app.snob.twitterapiclone.dto.LikeRequest
import app.snob.twitterapiclone.dto.PostRequest
import app.snob.twitterapiclone.dto.PostResponse
import app.snob.twitterapiclone.model.Comment
import app.snob.twitterapiclone.model.Feed
import app.snob.twitterapiclone.model.Post
import app.snob.twitterapiclone.model.User
import app.snob.twitterapiclone.repository.CommentRepository
import app.snob.twitterapiclone.repository.PostRepository
import app.snob.twitterapiclone.repository.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

class PostServiceSpec extends Specification {
    UserRepository userRepository
    PostRepository postRepository
    CommentRepository commentRepository
    PostService postService

    def setup() {
        userRepository = Mock()
        postRepository = Mock()
        commentRepository = Mock()
        postService = new PostService(userRepository, postRepository, commentRepository)
    }

    @Unroll
    def "savePost should save a new post"() {
        given:
        def postRequest = new PostRequest(userId: "userId", content: "Test content")
        def user = new User(id: "userId")
        userRepository.findById("userId") >> Optional.of(user)

        when:
        postService.savePost(postRequest)

        then:
        1 * userRepository.findById("userId") >> Optional.of(user)
        1 * postRepository.save(_)
    }

    @Unroll
    def "getAllPosts should return a list of all posts"() {
        given:
        def posts = [Post.builder().id("1").user(new User(id: "676")).content("Post 1").build(),
                     Post.builder().id("2").user(new User(id: "676")).content("Post 2").build()]
        postRepository.findAll() >> posts

        when:
        List<PostResponse> allPosts = postService.getAllPosts()

        then:
        allPosts.size() == 2
        allPosts*.content == ["Post 1", "Post 2"]
    }

    @Unroll
    def "getPostById should return the post with the specified ID"() {
        given:
        def postId = "postId"
        def post = Post.builder().id(postId).user(new User(id: "676")).content("Test content").build()
        postRepository.findById(postId) >> Optional.of(post)

        when:
        PostResponse postResponse = postService.getPostById(postId)

        then:
        postResponse.id == postId
        postResponse.content == "Test content"
    }

    @Unroll
    def "editPost should update the content of the post if content is provided"() {
        given:
        def postId = "postId"
        def postUpdateRequest = new PostRequest(content: "Updated content")
        def user = User.builder().id("userId").build()
        def post = Post.builder().id(postId).user(user).content("Original content").build()
        postRepository.findById(postId) >> Optional.of(post)

        when:
        postService.editPost(postId, postUpdateRequest)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)
        1 * postRepository.save({ it.content == "Updated content" } as Post)
    }

    @Unroll
    def "deletePostById should delete the post with the specified ID if it exists"() {
        given:
        def postId = "postId"
        postRepository.existsById(postId) >> true

        when:
        postService.deletePostById(postId)

        then:
        1 * postRepository.existsById(postId) >> true
        1 * postRepository.deleteById(postId)
    }

    @Unroll
    def "getAllPostsByUserSubscribe should return posts from users subscribed by the specified user"() {
        given:
        def userId = "userId"
        def subscriber = User.builder().id(userId).build()
        def subscriptions = [User.builder().id("subscribedUserId1").build(), User.builder().id("subscribedUserId2").build()]
        subscriber.subscriptions = subscriptions
        userRepository.findById(userId) >> Optional.of(subscriber)

        def posts1 = [Post.builder().id("1").content("Post 1").user(subscriptions[0]).build(),
                      Post.builder().id("2").content("Post 2").user(subscriptions[0]).build()]
        def posts2 = [Post.builder().id("3").content("Post 3").user(subscriptions[1]).build(),
                      Post.builder().id("4").content("Post 4").user(subscriptions[1]).build()]
        postRepository.findAllByUser(subscriptions[0]) >> posts1
        postRepository.findAllByUser(subscriptions[1]) >> posts2

        when:
        List<PostResponse> result = postService.getAllPostsByUserSubscribe(userId)

        then:
        result.size() == 4
        result*.content == ["Post 1", "Post 2", "Post 3", "Post 4"]
    }

    @Unroll
    def "addLikeToPost should add a like to the specified post"() {
        given:
        def likeRequest = new LikeRequest(postId: "postId", userId: "userId")
        def post = Post.builder()
                .id(likeRequest.postId)
                .user(User.builder().id("676").build())
                .likedByIds([])
                .build()
        postRepository.findById(likeRequest.postId) >> Optional.of(post)

        when:
        postService.addLikeToPost(likeRequest)

        then:
        1 * postRepository.save(_) >> { savedPost ->
            savedPost.likedByIds.contains(likeRequest.userId)
        }
    }

    @Unroll
    def "removeLikeFromPost should remove a like from the specified post"() {
        given:
        def likeRequest = new LikeRequest(postId: "postId", userId: "userId")
        def post = Post.builder().id(likeRequest.postId).user(User.builder().id("676").build()).likedByIds([likeRequest.userId]).build()
        postRepository.findById(likeRequest.postId) >> Optional.of(post)

        when:
        postService.removeLikeFromPost(likeRequest)

        then:
        1 * postRepository.save(_) >> { savedPost ->
            !savedPost.likedByIds.contains(likeRequest.userId)
        }
    }

    @Unroll
    def "getUserFeed should return a feed for the specified user"() {
        given:
        def userId = "userId"
        def subscriber = User.builder().id(userId).build()

        def subscribedUser1 = User.builder().id("subscribedUserId1").build()
        def subscribedUser2 = User.builder().id("subscribedUserId2").build()
        subscriber.subscriptions = [subscribedUser1, subscribedUser2]

        def favoritePost1 = Post.builder().id("favoritePost1").user(subscriber).build()
        def favoritePost2 = Post.builder().id("favoritePost2").user(subscriber).build()
        subscriber.favoritePosts = [favoritePost1, favoritePost2]

        userRepository.findById(userId) >> Optional.of(subscriber)

        def subscribedPosts1 = [Post.builder().id("subscribedPost1").user(subscribedUser1).build()]
        def subscribedPosts2 = [Post.builder().id("subscribedPost2").user(subscribedUser2).build()]
        postRepository.findAllByUser(subscribedUser1) >> subscribedPosts1
        postRepository.findAllByUser(subscribedUser2) >> subscribedPosts2

        def userComments1 = [Comment.builder().id("comment1").user(subscribedUser1).post(Post.builder().id("subscribedPost1").user(subscribedUser1).build()).build()]
        def userComments2 = [Comment.builder().id("comment2").user(subscribedUser2).post(Post.builder().id("subscribedPost1").user(subscribedUser1).build()).build()]
        commentRepository.findAllByUser(subscribedUser1) >> userComments1
        commentRepository.findAllByUser(subscribedUser2) >> userComments2

        when:
        Feed result = postService.getUserFeed(userId)

        then:
        result.userComments.size() == 2

        result.userComments*.id == ["comment1", "comment2"]
    }




    @Unroll
    def "getAnotherUserFeed should return a feed for another user"() {
        given:
        def userId = "userId"
        def user = User.builder().id(userId).build()
        def favoritePosts = [Post.builder().id("1").user(new User(id: "676")).build(), Post.builder().id("2").user(new User(id: "676")).build()]
        def post = new Post(id: "324", user: new User(id: "676"))
        def comments = [Comment.builder().id("123").post(post).user(new User(id: "676")).content("Comment 1").build(),
                        Comment.builder().id("156").post(post).user(new User(id: "676")).content("Comment 2").build()]
        userRepository.findById(userId) >> Optional.of(user)
        user.favoritePosts = favoritePosts
        commentRepository.findAllByUser(user) >> comments

        when:
        Feed result = postService.getAnotherUserFeed(userId)

        then:
        result.posts.size() == 2
        result.userComments.size() == 2
    }
}
