package app.snob.twitterapiclone.model

import groovy.transform.builder.Builder
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

import java.time.LocalDateTime

@Builder
@Document(collection = "post")
class Post {
    String id
    @DBRef
    User user
    String content
    LocalDateTime createdAt
}
