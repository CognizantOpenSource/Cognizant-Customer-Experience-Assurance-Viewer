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

package com.cognizant.authapi.users.controllers;

import com.cognizant.authapi.base.error.AccountNotFoundException;
import com.cognizant.authapi.base.error.RoleNotFoundException;
import com.cognizant.authapi.users.beans.Account;
import com.cognizant.authapi.users.beans.Role;
import com.cognizant.authapi.users.dto.AccountDTO;
import com.cognizant.authapi.users.services.AccountService;
import com.cognizant.authapi.users.services.RoleService;
import com.cognizant.authapi.users.util.AccUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Created by 784420 on 7/23/2019 3:40 PM
 */
@RestController
@RequestMapping(value = "/accounts")
@AllArgsConstructor
@Slf4j
public class AccountController {

    private AccountService accountService;
    private RoleService roleService;
    private AccUtil util;

    /**
     * Getting all Accounts
     **/
    @GetMapping(value = "")
    @PreAuthorize("hasPermission('Account','cxa.user.account.read')")
    public List<AccountDTO> getAllAccounts() {
        log.debug("Getting all the Accounts");
        List<Account> accounts = accountService.getAllAccounts();
        return util.convertToDtoList(accounts);
    }

    /**
     * Getting Account by accountId
     * @param accountId id of Account
     * @return {@link Account}
     */
    @GetMapping(value = "/{accountId}")
    @PreAuthorize("hasPermission('Account','cxa.user.account.read')")
    public Account getAccount(@PathVariable String accountId) {
        return accountService.assertOrGet(accountId);
    }

    /**
     * Updating a Account
     **/
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{accountId}")
    @Transactional
    @PreAuthorize("hasPermission('Account','cxa.user.account.update')")
    public AccountDTO updateAccount(@Valid @RequestBody AccountDTO accountDTO) {
        List<Role> roleList = addAllRoles(accountDTO.getRoleIds());
        log.debug("Updating the Account data " + accountDTO);
        Account account = assertOrGetAccount(accountDTO.getId());
        account = accountService.updateAccount(util.mergeToEntity(accountDTO, account, roleList));
        return util.convertToDto(account);

    }

    private Account assertOrGetAccount(String accountId) {
        Optional<Account> accountOptional = accountService.getAccount(accountId);
        if (!accountOptional.isPresent()) {
            throw new AccountNotFoundException(accountId);
        } else {
            return accountOptional.get();
        }
    }

    private List<Role> addAllRoles(List<String> roleIdList) {
        List<Role> roleList = (List<Role>) roleService.getAllRoleById(roleIdList);
        if (roleList.isEmpty())
            throw new RoleNotFoundException(roleIdList.toString());
        return roleList;
    }
}
