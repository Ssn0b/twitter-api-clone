package app.snob.twitterapiclone.model

import groovy.transform.builder.Builder
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document


@Builder
@Document(collection = "user")
class User {
    @Id
    String id
    String username
    String email
    String firstname
    String lastname
    @DBRef
    List<Post> favoritePosts = []
    @DBRef
    List<User> subscriptions = []

    List<Post> getFavoritePosts() {
        return favoritePosts
    }

    void setFavoritePosts(List<Post> favoritePosts) {
        this.favoritePosts = favoritePosts
    }
    List<User> getSubscriptions() {
        return subscriptions
    }

    void setSubscriptions(List<User> subscriptions) {
        this.subscriptions = subscriptions
    }
}
