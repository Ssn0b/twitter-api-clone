package app.snob.twitterapiclone.repository

import app.snob.twitterapiclone.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends MongoRepository<User, String> {
}