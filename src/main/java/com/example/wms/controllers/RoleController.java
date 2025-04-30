package com.example.wms.controllers;

import com.example.wms.model.dto.request.RoleInfoReq;
import com.example.wms.model.dto.response.RoleInfoResp;
import com.example.wms.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public RoleInfoResp createRole(@RequestBody RoleInfoReq req) {
        return roleService.createRole(req);
    }

    @GetMapping("/{id}")
    public RoleInfoResp getRole(@PathVariable Long id) {
        return roleService.getRole(id);
    }

    @GetMapping("/all")
    public Page<RoleInfoResp> getAllRoles(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer perPage,
                                          @RequestParam(defaultValue = "brand") String sort,
                                          @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return roleService.getAllRoles(page, perPage, sort, order);
    }

    @PutMapping("/{id}")
    public RoleInfoResp updateRole(@PathVariable Long id, @RequestBody RoleInfoReq req) {
        return roleService.updateRole(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }

}
