package app.snob.twitterapiclone.dto

import groovy.transform.builder.Builder

@Builder
class UserRequest {
    String username
    String email
    String firstname
    String lastname
}
