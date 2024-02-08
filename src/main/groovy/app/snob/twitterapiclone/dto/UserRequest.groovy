package app.snob.twitterapiclone.dto

import app.snob.twitterapiclone.model.Post
import app.snob.twitterapiclone.model.User
import groovy.transform.builder.Builder
import org.springframework.data.mongodb.core.mapping.DBRef

@Builder
class UserRequest {
    String username;
    String email;
    String firstname;
    String lastname;
    List<Post> favoritePosts = []
    List<User> subscriptions = []
}
