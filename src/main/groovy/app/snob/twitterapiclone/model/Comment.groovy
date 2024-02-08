package app.snob.twitterapiclone.model

import groovy.transform.builder.Builder
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

import java.time.LocalDateTime

@Builder
@Document(collection = "comment")
class Comment {
    @Id
    String id
    @DBRef
    User user
    @DBRef
    Post post
    String content
    LocalDateTime createdAt
}
