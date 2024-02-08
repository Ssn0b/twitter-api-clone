package app.snob.twitterapiclone.dto

import app.snob.twitterapiclone.model.User
import groovy.transform.builder.Builder

import java.time.LocalDateTime

@Builder
class PostResponse {
    String id
    User user
    String content
    LocalDateTime createdAt
}
