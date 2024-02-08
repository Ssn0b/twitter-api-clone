package app.snob.twitterapiclone.service

import app.snob.twitterapiclone.dto.CommentRequest
import app.snob.twitterapiclone.dto.CommentResponse
import app.snob.twitterapiclone.model.Comment
import app.snob.twitterapiclone.model.Post
import app.snob.twitterapiclone.model.User
import app.snob.twitterapiclone.repository.CommentRepository
import app.snob.twitterapiclone.repository.PostRepository
import app.snob.twitterapiclone.repository.UserRepository
import spock.lang.Specification
import spock.lang.Unroll


class CommentServiceSpec extends Specification {
    UserRepository userRepository
    PostRepository postRepository
    CommentRepository commentRepository
    CommentService commentService

    def setup() {
        userRepository = Mock()
        postRepository = Mock()
        commentRepository = Mock()
        commentService = new CommentService(userRepository, postRepository, commentRepository)
    }

    @Unroll
    def "commentOnPost should save comment on post"() {
        given:
        def postId = "postId"
        def userId = "userId"
        def content = "This is a test comment."
        def commentRequest = new CommentRequest(postId: postId, userId: userId, content: content)
        def post = new Post(id: postId)
        def user = new User(id: userId)
        postRepository.findById(postId) >> Optional.of(post)
        userRepository.findById(userId) >> Optional.of(user)

        when:
        commentService.commentOnPost(commentRequest)

        then:
        1 * commentRepository.save({ Comment comment ->
            comment.post == post && comment.user == user && comment.content == content
        } as Comment)
    }

    @Unroll
    def "getPostComments should return list of comment responses for a given post ID"() {
        given:
        def postId = "postId"
        def post = new Post(id: postId, user: new User(id: "676"))
        def comments = [Comment.builder().id("123").post(post).user(new User(id: "676")).content("Comment 1").build(),
                        Comment.builder().id("156").post(post).user(new User(id: "676")).content("Comment 2").build()]

        postRepository.findById(postId) >> Optional.of(post)
        commentRepository.findAllByPost(post) >> comments

        when:
        List<CommentResponse> result = commentService.getPostComments(postId)

        then:
        result.size() == 2
        result*.content == ["Comment 1", "Comment 2"]
    }
}
