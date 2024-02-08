package app.snob.twitterapiclone.dto

import groovy.transform.builder.Builder

@Builder
class LikeRequest {
    String userId
    String postId
}
