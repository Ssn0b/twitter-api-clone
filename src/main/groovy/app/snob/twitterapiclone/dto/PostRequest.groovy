package app.snob.twitterapiclone.dto

import groovy.transform.builder.Builder
import java.time.LocalDateTime

@Builder
class PostRequest {
    String userId
    String content
    LocalDateTime createdAt
}
