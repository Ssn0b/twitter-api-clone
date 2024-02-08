package app.snob.twitterapiclone.dto

import app.snob.twitterapiclone.model.Post
import app.snob.twitterapiclone.model.User
import groovy.transform.builder.Builder


@Builder
class UserResponse {
    String id;
    String username;
    String email;
    String firstname;
    String lastname;
    List<Post> favoritePosts = []
    List<User> subscriptions = []
}
