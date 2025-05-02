package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Role;
import com.example.wms.model.db.entity.User;
import com.example.wms.model.db.repository.RoleRepository;
import com.example.wms.model.db.repository.UserRepository;
import com.example.wms.model.dto.request.UserInfoReq;
import com.example.wms.model.dto.response.UserInfoResp;
import com.example.wms.service.UserService;
import com.example.wms.utils.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserInfoResp createUser(UserInfoReq req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new CommonBackendException("User  with username already exists", HttpStatus.CONFLICT);
        }

        User user = objectMapper.convertValue(req, User.class);

        if (req.getRoleName() != null) {
            Role role = roleRepository.findByNameAndIsActiveTrue(req.getRoleName())
                    .orElseThrow(() -> new CommonBackendException(
                            "Role with name: " + req.getRoleName() + " not found", HttpStatus.NOT_FOUND));
            user.setRole(role);
        } else {
            throw new CommonBackendException("Role must be provided", HttpStatus.BAD_REQUEST);
        }
        user.setIsActive(true);
        User savedUser = userRepository.save(user);
        UserInfoResp resp = objectMapper.convertValue(savedUser, UserInfoResp.class);
        resp.setRoleName(savedUser.getRole().getName());
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoResp getUser(Long id) {
        final String errMsg = String.format("User  with id: %s not found", id);

        User user = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        UserInfoResp resp = objectMapper.convertValue(user, UserInfoResp.class);
        resp.setRoleName(user.getRole().getName());
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserInfoResp> getAllUsers(Integer page, Integer perPage, String sort, Sort.Direction order) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<User> users;

        users = userRepository.findAllByIsActiveTrue(pageRequest);

        List<UserInfoResp> content = users.getContent().stream()
                .map(user -> {
                    UserInfoResp resp = objectMapper.convertValue(user, UserInfoResp.class);
                    resp.setRoleName(user.getRole().getName());
                    return resp;
                }).collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, users.getTotalElements());
    }

    @Override
    @Transactional
    public UserInfoResp updateUser(Long id, UserInfoReq req) {
        final String errUserMsg = String.format("User  with id: %s not found", id);

        User user = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errUserMsg, HttpStatus.NOT_FOUND));

        user.setUsername(req.getUsername() != null ? req.getUsername() : user.getUsername());
        user.setPasswordHash(req.getPasswordHash() != null ? req.getPasswordHash() : user.getPasswordHash());
        user.setFirstName(req.getFirstName() != null ? req.getFirstName() : user.getFirstName());
        user.setLastName(req.getLastName() != null ? req.getLastName() : user.getLastName());
        user.setMiddleName(req.getMiddleName() != null ? req.getMiddleName() : user.getMiddleName());

        if (req.getRoleName() != null) {
            final String errRoleMsg = String.format("Role with name: %s not found", req.getRoleName());
            Role role = roleRepository.findByNameAndIsActiveTrue(req.getRoleName())
                    .orElseThrow(() -> new CommonBackendException(errRoleMsg, HttpStatus.NOT_FOUND));
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        UserInfoResp resp = objectMapper.convertValue(updatedUser, UserInfoResp.class);
        resp.setRoleName(user.getRole().getName());
        return resp;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        final String errMsg = String.format("User  with id: %s not found", id);

        User user = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));
        user.setIsActive(false);
        userRepository.save(user);
    }

}