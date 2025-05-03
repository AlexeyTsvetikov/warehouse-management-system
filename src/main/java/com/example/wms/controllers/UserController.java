package com.example.wms.controllers;

import com.example.wms.model.dto.request.UserInfoReq;
import com.example.wms.model.dto.response.UserInfoResp;
import com.example.wms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserInfoResp createUser(@RequestBody UserInfoReq req) {
        return userService.createUser(req);
    }

    @GetMapping("/{id}")
    public UserInfoResp getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/all")
    public Page<UserInfoResp> getAllUsers(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer perPage,
                                          @RequestParam(defaultValue = "username") String sort,
                                          @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                          @RequestParam(required = false) String filter) {
        return userService.getAllUsers(page, perPage, sort, order, filter);
    }

    @PutMapping("/{id}")
    public UserInfoResp updateUser(@PathVariable Long id, @RequestBody UserInfoReq req) {
        return userService.updateUser(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
