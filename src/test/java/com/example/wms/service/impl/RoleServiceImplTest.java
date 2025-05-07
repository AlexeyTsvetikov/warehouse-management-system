package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Role;
import com.example.wms.model.db.repository.RoleRepository;
import com.example.wms.model.dto.request.RoleInfoReq;
import com.example.wms.model.dto.response.RoleInfoResp;
import com.example.wms.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void createRole() {
        RoleInfoReq req = new RoleInfoReq();
        req.setName("TestName");
        req.setDescription("TestDescription");

        Role role = new Role();
        role.setName(req.getName());
        role.setDescription(req.getDescription());
        role.setIsActive(true);

        when(roleRepository.findByName(req.getName())).thenReturn(Optional.empty());

        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleInfoResp roleInfoResp = roleService.createRole(req);

        assertEquals(role.getName(), roleInfoResp.getName());
        assertEquals(role.getDescription(), roleInfoResp.getDescription());
    }

    @Test
    void createRoleExists() {
        RoleInfoReq req = new RoleInfoReq();
        req.setName("ExistingName");

        Role existingRole = new Role();
        existingRole.setName("ExistingName");

        when(roleRepository.findByName(req.getName())).thenReturn(Optional.of(existingRole));

        assertThrows(CommonBackendException.class, () -> roleService.createRole(req));
    }

    @Test
    void getRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName("TestName");
        role.setDescription("TestDescription");
        role.setIsActive(true);

        when(roleRepository.findByIdAndIsActiveTrue(role.getId())).thenReturn(Optional.of(role));

        RoleInfoResp roleInfoResp = roleService.getRole(role.getId());
        assertEquals(role.getId(), roleInfoResp.getId());
        assertEquals(role.getName(), roleInfoResp.getName());
        assertEquals(role.getDescription(), roleInfoResp.getDescription());
    }

    @Test
    void getRoleNotFound() {
        Long nonExistingId = 999L;
        assertRoleNotFound(nonExistingId);
    }

    @Test
    void getAllRoles() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        Role role = new Role();
        RoleInfoResp roleInfoResp = new RoleInfoResp();

        when(roleRepository.findAllByIsActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(role)));
        when(objectMapper.convertValue(role, RoleInfoResp.class)).thenReturn(roleInfoResp);

        Page<RoleInfoResp> result = roleService.getAllRoles(pageNumber, pageSize, sortField, sortDirection);

        assertEquals(1, result.getContent().size());
        assertEquals(roleInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateRoleAllValues() {
        Role role = new Role();
        role.setId(1L);
        role.setName("TestName");
        role.setDescription("TestDescription");
        role.setIsActive(true);

        RoleInfoReq req = new RoleInfoReq();
        req.setName("NewName");
        req.setDescription("NewDescription");

        when(roleRepository.findByIdAndIsActiveTrue(role.getId())).thenReturn(Optional.of(role));

        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleInfoResp expectedResponse = new RoleInfoResp();
        expectedResponse.setId(role.getId());
        expectedResponse.setName(req.getName());
        expectedResponse.setDescription(req.getDescription());
        when(objectMapper.convertValue(role, RoleInfoResp.class)).thenReturn(expectedResponse);

        RoleInfoResp resp = roleService.updateRole(role.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getDescription(), resp.getDescription());
    }

    @Test
    void updateRoleNullValues() {
        Role role = new Role();
        role.setId(1L);
        role.setName("TestName");
        role.setDescription("TestDescription");
        role.setIsActive(true);

        RoleInfoReq req = new RoleInfoReq();
        req.setName(null);
        req.setDescription(null);

        when(roleRepository.findByIdAndIsActiveTrue(role.getId())).thenReturn(Optional.of(role));

        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleInfoResp expectedResponse = new RoleInfoResp();
        expectedResponse.setId(role.getId());
        expectedResponse.setName(role.getName());
        expectedResponse.setDescription(role.getDescription());
        when(objectMapper.convertValue(role, RoleInfoResp.class)).thenReturn(expectedResponse);

        RoleInfoResp resp = roleService.updateRole(role.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getName(), resp.getName());
        assertEquals(expectedResponse.getDescription(), resp.getDescription());
    }

    @Test
    void updateRoleNotFound() {
        Long nonExistingId = 999L;
        assertRoleNotFound(nonExistingId);
    }

    @Test
    void deleteRole() {
        Role role = new Role();
        role.setId(1L);
        when(roleRepository.findByIdAndIsActiveTrue(role.getId())).thenReturn(Optional.of(role));

        roleService.deleteRole(role.getId());
        verify(roleRepository, times(1)).save(any(Role.class));
        assertEquals(false, role.getIsActive());
    }

    @Test
    void deleteRoleNotFound() {
        Long nonExistingId = 999L;
        assertRoleNotFound(nonExistingId);
    }

    private void assertRoleNotFound(Long roleId) {
        when(roleRepository.findByIdAndIsActiveTrue(roleId)).thenReturn(Optional.empty());
        assertThrows(CommonBackendException.class, () -> roleService.getRole(roleId));
    }
}