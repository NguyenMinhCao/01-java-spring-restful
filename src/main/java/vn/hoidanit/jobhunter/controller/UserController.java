package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public List<User> getAllUser() {

        List<User> lstUser = this.userService.fetchAllUser();
        return lstUser;
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable long id) {

        return this.userService.fetchUserById(id);
    }

    @PostMapping("/user")
    public User createNewUser(@RequestBody User userPost) {

        User user = this.userService.handleCreateUser(userPost);
        return user;
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") long id) {

        this.userService.handleDeleteUser(id);
        return "detele success";
    }

    @PutMapping("/user")
    public User updateUser(@RequestBody User userPut) {
        User user = this.userService.handleUpdateUser(userPut);
        return user;
    }
}
