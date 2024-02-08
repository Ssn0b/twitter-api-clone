package app.snob.twitterapiclone.dto

import groovy.transform.builder.Builder

import java.time.LocalDateTime

@Builder
class PostResponse {
    String id
    String userId
    String content
    LocalDateTime createdAt
}
