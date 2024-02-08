package app.snob.twitterapiclone.controller

import app.snob.twitterapiclone.dto.UserRequest
import app.snob.twitterapiclone.dto.UserResponse
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
@RequestMapping("/api/v1/user")
class UserController {
    def userService

    UserController(UserService userService) {
        this.userService = userService
    }
    @PostMapping
    ResponseEntity<String> saveUser(@RequestBody UserRequest userRequest) {
        userService.saveUser(userRequest)
        return ResponseEntity.ok("User registered successfully")
    }

    @GetMapping
    List<UserResponse> getAllUsers() {
        userService.getAllUsers()
    }

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable(name = "id")  String id ) {
        userService.getUserById(id)
    }

    @PutMapping("/{id}")
    void editUser(@PathVariable(name = "id")  String id, @RequestBody UserRequest userUpdateRequest) {
        userService.editUser(id, userUpdateRequest)
    }

    @DeleteMapping("/{id}")
    void deleteUserById(@PathVariable(name = "id")  String id) {
        userService.deleteUserById(id)
    }

    @PostMapping("/{userId}/favorites/{postId}")
    void addToFavorite(@PathVariable(name = "userId") String userId, @PathVariable(name = "postId") String postId) {
        userService.addToFavorite(userId, postId)
    }

    @DeleteMapping("/{userId}/favorites/{postId}")
    void deleteFromFavorite(@PathVariable(name = "userId") String userId, @PathVariable(name = "postId") String postId) {
        userService.deleteFromFavorite(userId, postId)
    }

    @PostMapping("/{subscriberId}/subscriptions/{userId}")
    void subscribeToUser(@PathVariable(name = "subscriberId") String subscriberId, @PathVariable(name = "userId") String userId) {
        userService.subscribeToUser(subscriberId, userId)
    }

    @DeleteMapping("/{subscriberId}/subscriptions/{userId}")
    void unsubscribeFromUser(@PathVariable(name = "subscriberId") String subscriberId, @PathVariable(name = "userId") String userId) {
        userService.unsubscribeFromUser(subscriberId, userId)
    }
}
