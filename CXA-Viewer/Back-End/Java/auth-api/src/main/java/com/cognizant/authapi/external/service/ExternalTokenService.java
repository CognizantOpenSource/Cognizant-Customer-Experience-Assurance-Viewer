/*
 *
 *   Copyright (C) 2023 - Cognizant Technology Solutions
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.cognizant.authapi.external.service;

import com.cognizant.authapi.base.beans.JwtSecurityConstants;
import com.cognizant.authapi.base.services.JwtTokenService;
import com.cognizant.authapi.users.beans.*;
import com.cognizant.authapi.users.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cognizant.authapi.external.constants.ExternalConstants.ROBOT_PERMISSIONS;
import static com.cognizant.authapi.external.constants.ExternalConstants.TEST_PLUGIN_PERMISSIONS;

@Service
@AllArgsConstructor
public class ExternalTokenService {

    private JwtTokenService jwtTokenService;
    private UserService userService;

    public Map<String, Object> generateCurrentUserToken(User currentUser, int days) {
        Map<String, Object> map = jwtTokenService.generateToken(currentUser, days);
        map.put(JwtSecurityConstants.EXTERNAL_TOKEN, map.remove(JwtSecurityConstants.AUTH_TOKEN));
        map.put(JwtSecurityConstants.TOKEN_EXPIRES_AT, map.remove(JwtSecurityConstants.EXPIRES_AT));
        return map;
    }

    public Map<String, Object> generateTokenByRobot(@NotBlank String robotId, @NotBlank String robotName, int days) {
        return generateTokenByIdAndNameWithDays(robotId, robotName, days, ROBOT_PERMISSIONS);
    }

    public Map<String, Object> generateTokenForDashboard(String userId, String username, int days) {
        return generateTokenByIdAndNameWithDays(userId, username, days, TEST_PLUGIN_PERMISSIONS);
    }

    private Map<String, Object> generateTokenByIdAndNameWithDays(String id, String name, int days, List<String> permissions) {
        User user = generateUserByIdAndName(id, name, permissions);
        Map<String, Object> map = jwtTokenService.generateToken(user, days);
        map.put(JwtSecurityConstants.EXTERNAL_TOKEN, map.remove(JwtSecurityConstants.AUTH_TOKEN));
        map.put(JwtSecurityConstants.TOKEN_EXPIRES_AT, map.remove(JwtSecurityConstants.EXPIRES_AT));
        return map;
    }

    private User generateUserByIdAndName(@NotBlank String id, @NotBlank String name, List<String> permissions) {
        Account account = getAccount(id, permissions);
        return getUser(id, name, account);
    }

    private User getUser(@NotBlank String id, @NotBlank String name, Account account) {
        User user = new User();
        user.setId(id);
        user.setFirstName(name);
        user.setLastName(name);
        user.setEmail(name + "@auth-api-system.com");
        user.setAccount(account);
        user.setActive(true);
        return user;
    }

    private Account getAccount(@NotBlank String id, List<String> permissionList) {
        Account account = new Account();
        account.setId(id);
        account.setUserId(id);
        account.setOwnProjectIds(new ArrayList<>());
        account.setProjectIds(new ArrayList<>());
        if (CollectionUtils.isEmpty(permissionList)) {
            account.setRoles(new ArrayList<>());
        } else {
            Role role = new Role();
            role.setName("dummy");
            List<Permission> permissions = permissionList.stream().map(s -> {
                Permission permission = new Permission();
                permission.setId(s);
                return permission;
            }).collect(Collectors.toList());
            role.setPermissions(permissions);
            account.setRoles(Collections.singletonList(role));
        }
        return account;
    }

    public User getCurrentUser() {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (auth instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) auth;
            return userService.assertAndGetUser(userPrincipal.getId());
        } else {
            throw new AuthenticationCredentialsNotFoundException("user not signed in");
        }
    }
}
