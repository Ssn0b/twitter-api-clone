package app.snob.twitterapiclone.model

class Feed {
    List<Post> posts
    List<Comment> userComments

    Feed(List<Post> posts, List<Comment> userComments) {
        this.posts = posts
        this.userComments = userComments
    }
}
