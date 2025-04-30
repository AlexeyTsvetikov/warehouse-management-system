package com.example.wms.service;

import com.example.wms.model.dto.request.RoleInfoReq;
import com.example.wms.model.dto.response.RoleInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


public interface RoleService {
    RoleInfoResp createRole(RoleInfoReq req);

    @Transactional(readOnly = true)
    RoleInfoResp getRole(Long id);

    Page<RoleInfoResp> getAllRoles(Integer page, Integer perPage, String sort, Sort.Direction order);

    RoleInfoResp updateRole(Long id, RoleInfoReq req);

    void deleteRole(Long id);
}
