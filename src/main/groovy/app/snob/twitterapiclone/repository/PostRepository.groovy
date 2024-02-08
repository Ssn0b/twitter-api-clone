package app.snob.twitterapiclone.repository

import app.snob.twitterapiclone.model.Post
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository extends MongoRepository<Post, String> {
}

