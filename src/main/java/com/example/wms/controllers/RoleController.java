package com.example.wms.controllers;

import com.example.wms.model.dto.request.RoleInfoReq;
import com.example.wms.model.dto.response.RoleInfoResp;
import com.example.wms.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Роли")
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Создать роль")
    public RoleInfoResp createRole(@RequestBody @Valid RoleInfoReq req) {
        return roleService.createRole(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить роль по id")
    public RoleInfoResp getRole(@PathVariable Long id) {
        return roleService.getRole(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список ролей")
    public Page<RoleInfoResp> getAllRoles(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer perPage,
                                          @RequestParam(defaultValue = "name") String sort,
                                          @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return roleService.getAllRoles(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить роль по id")
    public RoleInfoResp updateRole(@PathVariable Long id, @RequestBody @Valid RoleInfoReq req) {
        return roleService.updateRole(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить роль по id")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }

}
