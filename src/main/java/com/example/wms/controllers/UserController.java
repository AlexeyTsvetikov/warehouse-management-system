package com.example.wms.controllers;

import com.example.wms.model.dto.request.UserInfoReq;
import com.example.wms.model.dto.response.UserInfoResp;
import com.example.wms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи")
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создать пользователя")
    @PreAuthorize("hasRole('ADMIN')")
    public UserInfoResp createUser(@RequestBody @Valid UserInfoReq req) {
        return userService.createUser(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public UserInfoResp getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список пользователей")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public Page<UserInfoResp> getAllUsers(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer perPage,
                                          @RequestParam(defaultValue = "username") String sort,
                                          @RequestParam(defaultValue = "ASC") Sort.Direction order,
                                          @RequestParam(required = false) String filter) {
        return userService.getAllUsers(page, perPage, sort, order, filter);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя по id")
    @PreAuthorize("hasRole('ADMIN')")
    public UserInfoResp updateUser(@PathVariable Long id, @RequestBody @Valid UserInfoReq req) {
        return userService.updateUser(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по id")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
