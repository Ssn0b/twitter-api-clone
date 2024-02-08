package app.snob.twitterapiclone.dto

import groovy.transform.builder.Builder

import java.time.LocalDateTime

@Builder
class CommentResponse {
    String id
    String userId
    String postId
    String content
    LocalDateTime createdAt
}
