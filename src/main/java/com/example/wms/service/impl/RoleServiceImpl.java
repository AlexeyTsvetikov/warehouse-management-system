package com.example.wms.service.impl;

import com.example.wms.exception.CommonBackendException;
import com.example.wms.model.db.entity.Role;
import com.example.wms.model.db.repository.RoleRepository;
import com.example.wms.model.dto.request.RoleInfoReq;
import com.example.wms.model.dto.response.RoleInfoResp;
import com.example.wms.service.RoleService;
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
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public RoleInfoResp createRole(RoleInfoReq req) {
        if (roleRepository.findByName(req.getName()).isPresent()) {
            throw new CommonBackendException("Role with name already exists", HttpStatus.CONFLICT);
        }

        Role role = objectMapper.convertValue(req, Role.class);
        role.setIsActive(true);

        Role savedRole = roleRepository.save(role);

        return objectMapper.convertValue(savedRole, RoleInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleInfoResp getRole(Long id) {
        final String errMsg = String.format("Role with id: %s not found", id);

        Role role = roleRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        return objectMapper.convertValue(role, RoleInfoResp.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleInfoResp> getAllRoles(Integer page, Integer perPage, String sort, Sort.Direction order) {

        Pageable pageRequest = PaginationUtils.getPageRequest(page, perPage, sort, order);
        Page<Role> roles = roleRepository.findAllByIsActiveTrue(pageRequest);

        List<RoleInfoResp> content = roles.getContent().stream()
                .map(role -> objectMapper.convertValue(role, RoleInfoResp.class))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, roles.getTotalElements());
    }

    @Override
    @Transactional
    public RoleInfoResp updateRole(Long id, RoleInfoReq req) {
        final String errMsg = String.format("Role with id: %s not found", id);

        Role role = roleRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        role.setName(req.getName() != null ? req.getName() : role.getName());
        role.setDescription(req.getDescription() != null ? req.getDescription() : role.getDescription());

        Role updatedRole = roleRepository.save(role);
        return objectMapper.convertValue(updatedRole, RoleInfoResp.class);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        final String errMsg = String.format("Role with id: %s not found", id);

        Role role = roleRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new CommonBackendException(errMsg, HttpStatus.NOT_FOUND));

        role.setIsActive(false);
        roleRepository.save(role);
    }

}
