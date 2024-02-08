package app.snob.twitterapiclone.controller

import app.snob.twitterapiclone.dto.CommentRequest
import app.snob.twitterapiclone.dto.CommentResponse
import app.snob.twitterapiclone.service.CommentService
import app.snob.twitterapiclone.service.PostService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comment")
class CommentController {
    def commentService

    CommentController(CommentService commentService) {
        this.commentService = commentService
    }

    @PostMapping
    void commentOnPost(@RequestBody CommentRequest commentRequest) {
        commentService.commentOnPost(commentRequest)
    }

    @GetMapping("/{postId}")
    List<CommentResponse> getPostComments(@PathVariable(name = "postId") String postId) {
        commentService.getPostComments(postId)
    }
}