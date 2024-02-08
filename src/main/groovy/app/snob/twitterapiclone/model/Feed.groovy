package app.snob.twitterapiclone.model

import app.snob.twitterapiclone.dto.CommentResponse
import app.snob.twitterapiclone.dto.PostResponse

class Feed {
    List<PostResponse> posts
    List<CommentResponse> userComments

    Feed(List<PostResponse> posts, List<CommentResponse> userComments) {
        this.posts = posts
        this.userComments = userComments
    }
}
