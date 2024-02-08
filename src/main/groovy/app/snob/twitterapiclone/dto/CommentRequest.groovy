package app.snob.twitterapiclone.dto

import groovy.transform.builder.Builder

@Builder
class CommentRequest {
    String userId
    String postId
    String content
}
