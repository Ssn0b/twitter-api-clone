package app.snob.twitterapiclone.repository

import app.snob.twitterapiclone.model.Comment
import app.snob.twitterapiclone.model.Post
import app.snob.twitterapiclone.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findAllByPost(Post post)
    List<Comment> findAllByUser(User user)
}