package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Role;
import com.example.wms.model.db.entity.User;
import com.example.wms.model.db.repository.RoleRepository;
import com.example.wms.model.db.repository.UserRepository;
import com.example.wms.model.dto.request.UserInfoReq;
import com.example.wms.model.dto.response.UserInfoResp;
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
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    void createUser() {
        Role role = new Role();
        role.setName("RoleName");
        role.setId(1L);

        UserInfoReq req = new UserInfoReq();
        req.setUsername("TestUsername");
        req.setPasswordHash("TestPasswordHash");
        req.setFirstName("TestFirstName");
        req.setLastName("TestLastName");
        req.setMiddleName("TestMiddleName");
        req.setRoleName(role.getName());

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPasswordHash(req.getPasswordHash());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setMiddleName(req.getMiddleName());
        user.setRole(role);

        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.empty());

        when(roleRepository.findByNameAndIsActiveTrue(req.getRoleName())).thenReturn(Optional.of(role));

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserInfoResp userInfoResp = userService.createUser(req);

        assertEquals(user.getUsername(), userInfoResp.getUsername());
        assertEquals(user.getFirstName(), userInfoResp.getFirstName());
        assertEquals(user.getLastName(), userInfoResp.getLastName());
        assertEquals(user.getMiddleName(), userInfoResp.getMiddleName());
        assertEquals(user.getRole().getName(), userInfoResp.getRoleName());
    }

    @Test
    void createUserExists() {
        UserInfoReq req = new UserInfoReq();
        req.setUsername("ExistingUsername");

        User existingUser = new User();
        existingUser.setUsername("ExistingUsername");

        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.of(existingUser));

        assertThrows(CommonBackendException.class, () -> userService.createUser(req));
    }

    @Test
    void createUserNotFoundRole() {
        UserInfoReq req = new UserInfoReq();
        req.setRoleName("NoRoleName");

        when(roleRepository.findByNameAndIsActiveTrue(req.getRoleName())).thenReturn(Optional.empty());

        assertThrows(CommonBackendException.class, () -> userService.createUser(req));
    }

    @Test
    void createUserNullRole() {
        UserInfoReq req = new UserInfoReq();
        req.setRoleName(null);

        CommonBackendException exception = assertThrows(CommonBackendException.class, () ->
                userService.createUser(req));
        assertEquals("Role must be provided", exception.getMessage());
    }


    @Test
    void getUser() {
        Role role = new Role();
        role.setName("RoleName");

        User user = new User();
        user.setId(1L);
        user.setUsername("TestUsername");
        user.setPasswordHash("TestPasswordHash");
        user.setFirstName("TestFirstName");
        user.setLastName("TestLastName");
        user.setMiddleName("TestMiddleName");
        user.setRole(role);
        user.setIsActive(true);

        when(userRepository.findByIdAndIsActiveTrue(user.getId())).thenReturn(Optional.of(user));

        UserInfoResp userInfoResp = userService.getUser(user.getId());
        assertEquals(user.getId(), userInfoResp.getId());
        assertEquals(user.getUsername(), userInfoResp.getUsername());
        assertEquals(user.getFirstName(), userInfoResp.getFirstName());
        assertEquals(user.getLastName(), userInfoResp.getLastName());
        assertEquals(user.getMiddleName(), userInfoResp.getMiddleName());
        assertEquals(user.getRole().getName(), userInfoResp.getRoleName());
    }

    @Test
    void getUserNotFound() {
        Long nonExistingId = 999L;
        assertUserNotFound(nonExistingId);
    }

    @Test
    void getAllUsersWithFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String filter = "active";

        Role role = new Role();
        role.setName("TestRole");

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        User user = new User();
        user.setRole(role);
        UserInfoResp userInfoResp = new UserInfoResp();

        when(userRepository.findAllFiltered(pageable, filter)).thenReturn(new PageImpl<>(List.of(user)));
        when(objectMapper.convertValue(user, UserInfoResp.class)).thenReturn(userInfoResp);

        Page<UserInfoResp> result = userService.getAllUsers(pageNumber, pageSize, sortField, sortDirection, filter);

        assertEquals(1, result.getContent().size());
        assertEquals(userInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllUsersWithoutFilter() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortField = "name";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        Role role = new Role();
        role.setName("TestRole");

        Pageable pageable = PaginationUtils.getPageRequest(pageNumber, pageSize, sortField, sortDirection);

        User user = new User();
        user.setRole(role);
        UserInfoResp userInfoResp = new UserInfoResp();

        when(userRepository.findAllByIsActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(user)));
        when(objectMapper.convertValue(user, UserInfoResp.class)).thenReturn(userInfoResp);

        Page<UserInfoResp> result = userService.getAllUsers(pageNumber, pageSize, sortField, sortDirection, null);

        assertEquals(1, result.getContent().size());
        assertEquals(userInfoResp, result.getContent().get(0));
        assertEquals(1, result.getTotalElements());

    }

    @Test
    void updateUserAllValues() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("RoleName");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("RoleName2");

        User user = new User();
        user.setId(1L);
        user.setUsername("TestUsername");
        user.setPasswordHash("TestPasswordHash");
        user.setFirstName("TestFirstName");
        user.setLastName("TestLastName");
        user.setMiddleName("TestMiddleName");
        user.setRole(role1);
        user.setIsActive(true);

        UserInfoReq req = new UserInfoReq();
        req.setRoleName(role2.getName());
        req.setUsername("NewUsername");
        req.setPasswordHash("NewPasswordHash");
        req.setFirstName("NewFirstName");
        req.setLastName("NewLastName");
        req.setMiddleName("NewMiddleName");

        when(userRepository.findByIdAndIsActiveTrue(user.getId())).thenReturn(Optional.of(user));

        when(roleRepository.findByNameAndIsActiveTrue(req.getRoleName())).thenReturn(Optional.of(role2));

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserInfoResp expectedResponse = new UserInfoResp();
        expectedResponse.setId(user.getId());
        expectedResponse.setUsername(req.getUsername());
        expectedResponse.setFirstName(req.getFirstName());
        expectedResponse.setMiddleName(req.getMiddleName());
        expectedResponse.setLastName(req.getLastName());
        expectedResponse.setRoleName(role2.getName());

        UserInfoResp resp = userService.updateUser(user.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getFirstName(), resp.getFirstName());
        assertEquals(expectedResponse.getLastName(), resp.getLastName());
        assertEquals(expectedResponse.getMiddleName(), resp.getMiddleName());
        assertEquals(expectedResponse.getUsername(), resp.getUsername());
        assertEquals(expectedResponse.getRoleName(), resp.getRoleName());

    }

    @Test
    void updateUserNullValues() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("RoleName");

        User user = new User();
        user.setId(1L);
        user.setUsername("TestUsername");
        user.setPasswordHash("TestPasswordHash");
        user.setFirstName("TestFirstName");
        user.setLastName("TestLastName");
        user.setMiddleName("TestMiddleName");
        user.setRole(role1);
        user.setIsActive(true);

        UserInfoReq req = new UserInfoReq();
        req.setRoleName(null);
        req.setUsername(null);
        req.setPasswordHash(null);
        req.setFirstName(null);
        req.setLastName(null);
        req.setMiddleName(null);

        when(userRepository.findByIdAndIsActiveTrue(user.getId())).thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserInfoResp expectedResponse = new UserInfoResp();
        expectedResponse.setId(user.getId());
        expectedResponse.setUsername(user.getUsername());
        expectedResponse.setFirstName(user.getFirstName());
        expectedResponse.setMiddleName(user.getMiddleName());
        expectedResponse.setLastName(user.getLastName());
        expectedResponse.setRoleName(user.getRole().getName());

        UserInfoResp resp = userService.updateUser(user.getId(), req);

        assertEquals(expectedResponse.getId(), resp.getId());
        assertEquals(expectedResponse.getFirstName(), resp.getFirstName());
        assertEquals(expectedResponse.getLastName(), resp.getLastName());
        assertEquals(expectedResponse.getMiddleName(), resp.getMiddleName());
        assertEquals(expectedResponse.getUsername(), resp.getUsername());
        assertEquals(expectedResponse.getRoleName(), resp.getRoleName());
    }

    @Test
    void updateUserRoleNotFound() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("RoleName");

        User user = new User();
        user.setId(1L);
        user.setUsername("TestUsername");
        user.setPasswordHash("TestPasswordHash");
        user.setFirstName("TestFirstName");
        user.setLastName("TestLastName");
        user.setMiddleName("TestMiddleName");
        user.setRole(role1);
        user.setIsActive(true);

        UserInfoReq req = new UserInfoReq();
        req.setRoleName("RoleNotFound");
        req.setUsername(null);
        req.setPasswordHash(null);
        req.setFirstName(null);
        req.setLastName(null);
        req.setMiddleName(null);

        when(userRepository.findByIdAndIsActiveTrue(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByNameAndIsActiveTrue(req.getRoleName())).thenReturn(Optional.empty());

        CommonBackendException ex = assertThrows(CommonBackendException.class, () ->
                userService.updateUser(user.getId(), req));
        assertEquals(String.format("Role with name: %s not found", req.getRoleName()), ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void updateUserNotFound() {
        Long nonExistingId = 999L;
        assertUserNotFound(nonExistingId);
    }

    @Test
    void deleteUser() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByIdAndIsActiveTrue(user.getId())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(false, user.getIsActive());
    }

    @Test
    void deleteUserNotFound() {
        Long nonExistingId = 999L;
        assertUserNotFound(nonExistingId);
    }

    private void assertUserNotFound(Long userId) {
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());
        assertThrows(CommonBackendException.class, () -> userService.getUser(userId));
    }
}