package app.snob.twitterapiclone.controller

import app.snob.twitterapiclone.dto.LikeRequest
import app.snob.twitterapiclone.dto.PostRequest
import app.snob.twitterapiclone.dto.PostResponse
import app.snob.twitterapiclone.model.Feed
import app.snob.twitterapiclone.service.PostService
import app.snob.twitterapiclone.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/post")
class PostController {
    def postService

    PostController(PostService postService) {
        this.postService = postService
    }

    @PostMapping
    ResponseEntity<String> savePost(@RequestBody PostRequest postRequest) {
        postService.savePost(postRequest)
        return ResponseEntity.ok("Post added successfully")
    }

    @GetMapping
    List<PostResponse> getAllPosts() {
        postService.getAllPosts()
    }

    @GetMapping("/{id}")
    PostResponse getPostById(@PathVariable(name = "id") String id) {
        postService.getPostById(id)
    }

    @PutMapping("/{id}")
    void editPost(@PathVariable(name = "id") String id, @RequestBody PostRequest postUpdateRequest) {
        postService.editPost(id, postUpdateRequest)
    }

    @DeleteMapping("/{id}")
    void deletePostById(@PathVariable(name = "id") String id) {
        postService.deletePostById(id)
    }

    @GetMapping("/user/subscribes/{userId}")
    List<PostResponse> getAllPostsByUserSubscribe(@PathVariable(name = "userId") String userId) {
        postService.getAllPostsByUserSubscribe(userId)
    }

    @PostMapping("/like/add")
    void addLikeToPost(@RequestBody LikeRequest likeRequest) {
        postService.addLikeToPost(likeRequest)
    }

    @PostMapping("like/remove")
    void removeLikeFromPost(@RequestBody LikeRequest likeRequest) {
        postService.removeLikeFromPost(likeRequest)
    }

    @GetMapping("/feed/{userId}")
    Feed getUserFeed(@PathVariable(name = "userId") String userId) {
        postService.getUserFeed(userId)
    }

    @GetMapping("/user/feed/{userId}")
    Feed getAnotherUserFeed(@PathVariable(name = "userId") String userId) {
        postService.getAnotherUserFeed(userId)
    }
}
