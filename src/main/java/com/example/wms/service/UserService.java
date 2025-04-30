package com.example.wms.service;

import com.example.wms.model.dto.request.UserInfoReq;
import com.example.wms.model.dto.response.UserInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


public interface UserService {
    UserInfoResp createUser(UserInfoReq req);

    @Transactional(readOnly = true)
    Page<UserInfoResp> getAllUsers(Integer page, Integer perPage, String sort, Sort.Direction order);

    UserInfoResp updateUser(Long userId, UserInfoReq req);

    void deleteUser(Long userId);

    UserInfoResp getUser(Long id);
}
